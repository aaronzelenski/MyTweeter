package edu.byu.cs.tweeter.server.dao;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Feed;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.BatchPostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.lambda.PostStatusHandler;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class StatusDAO extends BaseDAO implements IStatusDAO {

    //https://developer.android.com/studio/releases#jdk-macro


    private static final String AliasAttribute = "alias";
    private static final String TimestampAttribute = "timestamp";

    private static final Logger logger = Logger.getLogger(PostStatusHandler.class.getName());


    public StatusDAO() {
        super();
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

            statusTable.putItem(status); // Save the status object

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
            PageIterable<Status> pages = statusTable.query(enhancedRequest);

            boolean hasMorePages = false;
            for (Page<Status> page : pages) {
                statuses.addAll(page.items());
                if (page.lastEvaluatedKey() != null) {
                    hasMorePages = true;
                    break;
                }
            }

            // want to sort the statuses by timestamp

            return new GetStoryResponse(statuses, hasMorePages);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting story for user: " + userAlias, e);
            throw new RuntimeException("Failed to retrieve the story: " + e.getMessage());
        }
    }


    @Override
    public DataPage<Feed> getFeed(GetFeedRequest request) {
        try {
                QueryConditional queryConditional = QueryConditional
                        .keyEqualTo(Key.builder().partitionValue(request.getUser().getAlias()).build());

                QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional)
                        .limit(request.getLimit()); // You may want to adjust this for pagination

                QueryEnhancedRequest enhancedRequest = requestBuilder.build();
                DataPage<Feed> result = new DataPage<Feed>();
                PageIterable<Feed> pages = feedDynamoDbTable.query(enhancedRequest);

                pages.stream()
                        .limit(1)
                        .forEach((Page<Feed> page) -> {
                            result.setHasMorePages(page.lastEvaluatedKey() != null);
                            page.items().forEach(feeds -> result.getValues().add(feeds));
                        });
                return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting feed for user: " + request.getUser().getAlias(), e);
            throw new RuntimeException("Failed to retrieve the feed: " + e.getMessage());
        }
    }
    @Override
    public void batchUpdateFeeds(List<Feed> newFeeds) {
        if(newFeeds.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<Feed> table = enhancedClient.table(feedDynamoDbTable.tableName(), TableSchema.fromBean(Feed.class));
        WriteBatch.Builder<Feed> writeBuilder = WriteBatch.builder(Feed.class).mappedTableResource(table);
        for (Feed item : newFeeds) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                batchUpdateFeeds(result.unprocessedPutItemsForTable(table));
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }






}










//        try {
//                Feed feed = new Feed();
//                feed.setUserAlias(status.getUser().getAlias());
//                feed.setTimestamp(status.getTimestamp());
//                feed.setPost(status.getPost());
//                feed.setUser(status.getUser());
//                feed.setUrls(status.getUrls());
//                feed.setMentions(status.getMentions());
//
//                for (String followerAlias : followerAliases) {
//                feed.setFollowerAlias(followerAlias);
//                feedDynamoDbTable.putItem(feed);
//                }
//                } catch (Exception e) {
//                logger.log(Level.SEVERE, "Error updating feeds", e);
//                throw new RuntimeException("Failed to update feeds: " + e.getMessage());
//                }