package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.service.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.service.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.service.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.service.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.lambda.FollowHandler;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends BaseDAO implements IFollowDAO {


    private static final String TableName = "follows";
    private static final String FollowerHandleAttr = "follower_handle";
    private static final String FolloweeHandleAttr = "followee_handle";
    public static final String IndexName = "follows_index";
    private static final Logger logger = Logger.getLogger(FollowHandler.class.getName());


    public FollowDAO() {
        super();
    }


    @Override
    public FollowResponse follow(FollowRequest request) {

        UserDAO userDAO = new UserDAO();
        Follow follow = new Follow();
        try{

            GetUserRequest getFollowerUserRequest = new GetUserRequest(request.getFollowerAlias(), request.getAuthToken());
            GetUserResponse getUserFollowerResponse = userDAO.getUser(getFollowerUserRequest);
            User follower = getUserFollowerResponse.getUser();

            GetUserRequest getFolloweeUserRequest = new GetUserRequest(request.getFolloweeAlias(), request.getAuthToken());
            GetUserResponse getUserFolloweeResponse = userDAO.getUser(getFolloweeUserRequest);
            User followee = getUserFolloweeResponse.getUser();

        follow.setFollower(follower);
        follow.setFollowee(followee);

        follow.setFollower_handle(request.getFollowerAlias());
        follow.setFollowee_handle(request.getFolloweeAlias());

        follow.setFollowerName(follower.getFirstName());
        follow.setFolloweeName(followee.getFirstName());

        followDynamoDbTable.putItem(follow);

        return new FollowResponse();
        }
        catch (Exception e) {
            return new FollowResponse("Failed to follow user: " + e.getMessage());
        }
    }

    @Override
    public UnfollowResponse unfollow(UnfollowRequest request) {
        Follow follow = new Follow();
        UserDAO userDAO = new UserDAO();
        try{

            GetUserRequest getUnFollowerUserRequest = new GetUserRequest(request.getUnFollowerAlias(), request.getAuthToken());
            GetUserResponse getUserUnFollowerResponse = userDAO.getUser(getUnFollowerUserRequest);
            User unFollower = getUserUnFollowerResponse.getUser();

            GetUserRequest getUnFolloweeUserRequest = new GetUserRequest(request.getUnFolloweeAlias(), request.getAuthToken());
            GetUserResponse getUserUnFolloweeResponse = userDAO.getUser(getUnFolloweeUserRequest);
            User unFollowee = getUserUnFolloweeResponse.getUser();



            follow.setFollower(unFollower);
            follow.setFollowee(unFollowee);

            follow.setFollower_handle(request.getUnFollowerAlias());
            follow.setFollowee_handle(request.getUnFolloweeAlias());

            follow.setFollowerName(unFollower.getFirstName());
            follow.setFolloweeName(unFollowee.getFirstName());

            followDynamoDbTable.deleteItem(follow);

            return new UnfollowResponse();
        }
        catch (Exception e) {
            return new UnfollowResponse("Failed to unfollow user: " + e.getMessage());
        }
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        Follow follow = new Follow();

        boolean isFollower = false;

        try{

            follow.setFollower(request.getFollower());
            follow.setFollowee(request.getFollowee());

            follow.setFollower_handle(request.getFollower().getAlias());
            follow.setFollowee_handle(request.getFollowee().getAlias());

            follow.setFollowerName(request.getFollower().getFirstName());
            follow.setFolloweeName(request.getFollowee().getFirstName());

            Follow retirvedFollow = followDynamoDbTable.getItem(follow);

            if(retirvedFollow != null){
                isFollower = true;
            }

            return new IsFollowerResponse(isFollower);
        }
        catch (Exception e) {
            return new IsFollowerResponse("Failed to check if user is a follower: " + e.getMessage());
        }
    }

    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        try {
            DynamoDbIndex<Follow> index = followDynamoDbTable.index("follows_index");

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder()
                            .partitionValue(request.getAlias())
                            .build());

            QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .build();

            SdkIterable<Page<Follow>> pages = index.query(queryEnhancedRequest);

            int numOfFollowers = 0;

            for (Page<Follow> page : pages) {
                numOfFollowers += page.items().size();
            }

            return new GetFollowersCountResponse(numOfFollowers);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get followers count: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get followers count: " + e.getMessage(), e);
        }
    }





    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        try {

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder()
                            .partitionValue(request.getAlias())
                            .build());

            QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .build();

            SdkIterable<Page<Follow>> pages = followDynamoDbTable.query(queryEnhancedRequest);

            int numOfFollowing = 0;
            for (Page<Follow> page : pages) {
                numOfFollowing += page.items().size();
            }

            return new GetFollowingCountResponse(numOfFollowing);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get following count: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get following count: " + e.getMessage(), e);
        }
    }



    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {

        List<Follow> follows = new ArrayList<>();
        try {

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder()
                            .partitionValue(request.getFollowerAlias()) // The user whose following list we want
                            .build());

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .limit(request.getLimit());

//            logger.info(request.getFollowerAlias() + " is the user we are getting the following list for"); // this is working

            if (isNonEmptyString(request.getLastFolloweeAlias())) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put(FollowerHandleAttr, AttributeValue.builder().s(request.getFollowerAlias()).build());
                startKey.put(FolloweeHandleAttr, AttributeValue.builder().s(request.getLastFolloweeAlias()).build());
                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
            DataPage<Follow> result = new DataPage<>();

            PageIterable<Follow> pages = followDynamoDbTable.query(queryEnhancedRequest);
            pages.stream().limit(1).forEach(page -> {
                result.setHasMorePages(page.lastEvaluatedKey() != null);
                page.items().forEach(follow -> result.getValues().add(follow));
            });

            follows = result.getValues();
            List<User> followers = new ArrayList<>();


            for (Follow follow : follows) {
                User foll = convertHandleToUser(follow.getFollowee_handle());
                follow.setFollowee(foll);
                followers.add(foll);
            }

            return new FollowingResponse(followers, result.isHasMorePages());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get following list: " + e.getMessage(), e);
        }
    }

    private static boolean isNonEmptyString(String value) {
        return value != null && !value.isEmpty();
    }
    @Override
    public DataPage<Follow> getFollowers(GetFollowersRequest request) {

        DynamoDbIndex<Follow> index = followDynamoDbTable.index(IndexName);
        Key key = Key.builder()
                .partitionValue(request.getFolloweeAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());

        if (isNonEmptyString(request.getLastFollowerAlias())) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeHandleAttr, AttributeValue.builder().s(request.getFolloweeAlias()).build());
            startKey.put(FollowerHandleAttr, AttributeValue.builder().s(request.getLastFollowerAlias()).build());
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();

        DataPage<Follow> result = new DataPage<>();
        SdkIterable<Page<Follow>> sdkIterable = index.query(queryEnhancedRequest);
        PageIterable<Follow> pages = PageIterable.create(sdkIterable);

        pages.stream()
                .limit(1)
                .forEach((Page<Follow> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(feeds -> result.getValues().add(feeds));
                });

        return result;
    }


    @Override
    public User convertHandleToUser(String handle) {
        UserDAO userDAO = new UserDAO();
        return userDAO.userTable.getItem(Key.builder().partitionValue(handle).build());
    }


    @Override
    public void addFollowBatch(List<String> followerAliases, String followTarget) {
        List<Follow> batchToWrite = new ArrayList<>();
        for (String aFollowerAlias : followerAliases) {
            Follow follow = new Follow();
            follow.setFollower_handle(aFollowerAlias);
            follow.setFollowee_handle(followTarget);


            batchToWrite.add(follow);

            if (batchToWrite.size() == 10) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<Follow> follows) {
        if(follows.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
        WriteBatch.Builder<Follow> writeBuilder = WriteBatch.builder(Follow.class).mappedTableResource(table);
        for (Follow item : follows) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }






}
