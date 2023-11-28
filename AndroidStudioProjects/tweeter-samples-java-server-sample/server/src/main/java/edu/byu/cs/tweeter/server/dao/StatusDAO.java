package edu.byu.cs.tweeter.server.dao;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.lambda.FollowHandler;
import edu.byu.cs.tweeter.server.lambda.PostStatusHandler;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class StatusDAO implements IStatusDAO {


    private static final String TableName = "status";

    private final DynamoDbTable<Status> statusDynamoDbTable;
    private static final String AliasAttribute = "alias";
    private static final String TimestampAttribute = "timestamp";

    private DynamoDbClient dynamoDbClient;

    private static final Logger logger = Logger.getLogger(PostStatusHandler.class.getName());


    public StatusDAO() {
        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.statusDynamoDbTable = enhancedClient.table(TableName, TableSchema.fromBean(Status.class));
    }

    @Override
    public PostStatusResponse postStatus(PostStatusRequest request) {
        try {
            Status status = new Status();
            status.setAlias(request.getStatus().getUser().getAlias());

            status.setTimestamp(System.currentTimeMillis());

            status.setPost(request.getStatus().getPost());
            status.setUser(request.getStatus().getUser());
            status.setUrls(request.getStatus().getUrls());
            status.setMentions(request.getStatus().getMentions());

            statusDynamoDbTable.putItem(status); // Save the status object
            return new PostStatusResponse(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error posting status", e);
            return new PostStatusResponse("Failed to post status: " + e.getMessage());
        }
    }

    @Override
    public GetStoryResponse getStory(GetStoryRequest request) {
        List<Status> statuses = new ArrayList<>();
        String userAlias = request.getUser().getAlias();
        int limit = request.getLimit();
        Status lastStatus = request.getLastStatus();

        try {
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(userAlias).build());

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .limit(limit);

            if (lastStatus != null && lastStatus.getTimestamp() != null && lastStatus.getUser() != null) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put(AliasAttribute, AttributeValue.builder().s(lastStatus.getUser().getAlias()).build());
                startKey.put(TimestampAttribute, AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());
                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest enhancedRequest = requestBuilder.build();
            PageIterable<Status> pages = statusDynamoDbTable.query(enhancedRequest);

            boolean hasMorePages = false;
            for (Page<Status> page : pages) {
                statuses.addAll(page.items());
                if (page.lastEvaluatedKey() != null) {
                    hasMorePages = true;
                    break;
                }
            }

            return new GetStoryResponse(statuses, hasMorePages);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting story for user: " + userAlias, e);
            throw new RuntimeException("Failed to retrieve the story: " + e.getMessage());
        }
    }



    @Override
    public GetFeedResponse getFeed(GetFeedRequest request) {
        List<Status> feed = new ArrayList<>();
        boolean hasMorePages = false;
        try {
            List<String> followingUsers = getFollowingUsers(request.getUser().getAlias());

            for (String followingUserAlias : followingUsers) {
                QueryConditional queryConditional = QueryConditional
                        .keyEqualTo(Key.builder().partitionValue(followingUserAlias).build());

                QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .limit(request.getLimit()); // You may want to adjust this for pagination

                QueryEnhancedRequest enhancedRequest = requestBuilder.build();
                PageIterable<Status> pages = statusDynamoDbTable.query(enhancedRequest);

                for (Page<Status> page : pages) {
                    feed.addAll(page.items());
                    if (page.lastEvaluatedKey() != null) {
                        hasMorePages = true;
                    }
                }
            }

            feed.sort(Comparator.comparing(Status::getTimestamp).reversed()); // Sort by timestamp in descending order
            return new GetFeedResponse(feed, hasMorePages);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting feed for user: " + request.getUser().getAlias(), e);
            throw new RuntimeException("Failed to retrieve the feed: " + e.getMessage());
        }
    }

    private List<String> getFollowingUsers(String userAlias) {
        // TODO: Implement fetching of following users from the appropriate DynamoDB table or index

        FollowDAO followDAO = new FollowDAO();
        List<String> followingUsers = new ArrayList<>();
        String lastFolloweeAlias = null;
        boolean hasMorePages;

        do{
            FollowingRequest request = new FollowingRequest(new AuthToken(), userAlias, 10, lastFolloweeAlias);
            FollowingResponse response = followDAO.getFollowing(request);
            List<User> follows = response.getFollowees();
            for (User user : follows) {
                followingUsers.add(user.getAlias());
            }

            hasMorePages = response.getHasMorePages();
            if (hasMorePages) {
                lastFolloweeAlias = follows.get(follows.size() - 1).getAlias();
            }

        } while (hasMorePages);
        return followingUsers;
    }
}
