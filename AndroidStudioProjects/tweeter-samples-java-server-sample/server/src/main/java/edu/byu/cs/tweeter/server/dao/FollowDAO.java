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
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements IFollowDAO {


    private static final String TableName = "follows";
    private static final String FollowerHandleAttr = "follower_handle";
    private static final String FolloweeHandleAttr = "followee_handle";
    public static final String IndexName = "follows_index";
    public final DynamoDbTable<Follow> followDynamoDbTable;
    private static final Logger logger = Logger.getLogger(FollowHandler.class.getName());


    public FollowDAO() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.followDynamoDbTable = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
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
//            logger.log(Level.SEVERE, "Failed to get following list: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get following list: " + e.getMessage(), e);
        }
    }

    private static boolean isNonEmptyString(String value) {
        return value != null && !value.isEmpty();
    }
    @Override
    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        List<User> followers = new ArrayList<>();
        final boolean[] hasMorePages = new boolean[1]; // A lambda expression can only access final variables

        try {
            DynamoDbIndex<Follow> index = followDynamoDbTable.index(IndexName);

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder()
                            .partitionValue(request.getFolloweeAlias()) // This should be the user being followed
                            .build());

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .limit(request.getLimit());

            if (request.getLastFollowerAlias() != null && !request.getLastFollowerAlias().isEmpty()) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put(FolloweeHandleAttr, AttributeValue.builder().s(request.getFolloweeAlias()).build());
                startKey.put(FollowerHandleAttr, AttributeValue.builder().s(request.getLastFollowerAlias()).build());
                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
            SdkIterable<Page<Follow>> pages = index.query(queryEnhancedRequest);

            pages.stream().limit(1).forEach(page -> {
                hasMorePages[0] = page.lastEvaluatedKey() != null;
                page.items().forEach(follow -> {
                    User follower = convertHandleToUser(follow.getFollower_handle());
                    followers.add(follower);
                });
            });

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get followers: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get followers: " + e.getMessage(), e);
        }

        return new GetFollowersResponse(followers, hasMorePages[0]);
    }


    @Override
    public User convertHandleToUser(String handle) {
        UserDAO userDAO = new UserDAO();
        return userDAO.userTable.getItem(Key.builder().partitionValue(handle).build());
    }
}
