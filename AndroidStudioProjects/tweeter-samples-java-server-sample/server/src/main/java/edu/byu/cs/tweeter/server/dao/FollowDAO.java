package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.service.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.service.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.service.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.service.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.lambda.FollowHandler;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements IFollowDAO {


    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";
    private static final String FollowerHandleAttr = "follower_handle";
    private static final String FolloweeHandleAttr = "followee_handle";

    private final DynamoDbTable<Follow> followDynamoDbTable;

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

        Follow follow = new Follow();
        try{

        follow.setFollower(request.getFollower());
        follow.setFollowee(request.getFollowee());

        follow.setFollower_handle(request.getFollower().getAlias());
        follow.setFollowee_handle(request.getFollowee().getAlias());

        follow.setFollowerName(request.getFollower().getFirstName());
        follow.setFolloweeName(request.getFollowee().getFirstName());

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
        try{

            follow.setFollower(request.getUnFollower());
            follow.setFollowee(request.getUnFollowee());

            follow.setFollower_handle(request.getUnFollower().getAlias());
            follow.setFollowee_handle(request.getUnFollowee().getAlias());

            follow.setFollowerName(request.getUnFollower().getFirstName());
            follow.setFolloweeName(request.getUnFollowee().getFirstName());

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


    // used for getting a page of followers
    private static boolean isNonEmptyString(String value) {
        return value != null && !value.isEmpty();
    }



    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    public Integer getFolloweeCount(User follower) { // this function was given to me from github
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert follower != null;
        return getDummyFollowees().size();
    }


    public Integer getFollowerCount(User followee) { // NOT SURE IF THIS IS THE RIGHT VARIABLE (i made this function)
        assert followee != null;
        return getDummyFollowers().size();
    }









    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param followerAlias the alias of the user whose followees are to be returned
     * @param limit the number of followees to be returned in one page
     * @param lastFolloweeAlias the alias of the last followee in the previously retrieved page or
     *                          null if there was no previous request.
     * @return the followees.
     */
    public Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFolloweeAlias) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert limit > 0;
        assert followerAlias != null;

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(limit);

        boolean hasMorePages = false;

        if(limit > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(lastFolloweeAlias, allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < limit; followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new Pair<>(responseFollowees, hasMorePages);
    }

    public Pair<List<User>, Boolean> getFollowers(String followerAlias, int limit, String lastFollowerAlias) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert limit > 0;
        assert followerAlias != null;

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(limit);

        boolean hasMorePages = false;

        if(limit > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(lastFollowerAlias, allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < limit; followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new Pair<>(responseFollowees, hasMorePages);
    }





    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }


    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }


}
