package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "FollowTask";
    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";

    private User followee;

    private User follower;

    private String followerAlias;

    private String followeeAlias;


    private ServerFacade serverFacade;

    private FollowResponse response;



    // AuthoToken for the logged-in user. (This user is the "follower" in the relationship.)
    // User that is being followed.
    public FollowTask(StatusService service, AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;

    }


    @Override
    protected void runTask() {
        try {

            followerAlias = follower.getAlias();
            followeeAlias = followee.getAlias();


            FollowRequest request = new FollowRequest(followerAlias, followeeAlias, authToken);
            response = getServerFacade().follow(request, StatusService.TO_FOLLOW_URL);
//            this.followee = request.getFollowee();
//            this.follower = request.getFollower();
            System.out.println("follower: " + follower);
            System.out.println("followee: " + followee);
            sendSuccessMessage();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }


    private ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }
        return serverFacade;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(FollowTask.SUCCESS_KEY, response.isSuccess());
        msgBundle.putString(FollowTask.MESSAGE_KEY, response.getMessage());
    }
}
