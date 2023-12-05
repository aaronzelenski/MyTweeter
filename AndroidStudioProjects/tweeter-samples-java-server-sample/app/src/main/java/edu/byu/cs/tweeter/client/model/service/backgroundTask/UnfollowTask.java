package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask{
    private static final String LOG_TAG = "UnfollowTask";
    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";

    /**
     * The user that is being followed.
     */
    private User unFollowee;

    private User unFollower;

    private String unFollowerAlias;

    private String unFolloweeAlias;

    private ServerFacade serverFacade;

    private UnfollowResponse response;


    // AuthoToken for the logged-in user. (This user is the "follower" in the relationship.)
    // User that is being followed.
    public UnfollowTask(AuthToken authToken, User unFollower, User unFollowee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.unFollower = unFollower;
        this.unFollowee = unFollowee;
    }

    @Override
    protected void runTask() {
        try {

            unFollowerAlias = unFollower.getAlias();
            unFolloweeAlias = unFollowee.getAlias();

            UnfollowRequest request = new UnfollowRequest(authToken, unFollowerAlias, unFolloweeAlias);
            response = getServerFacade().unfollow(request, FollowService.TO_UNFOLLOW_URL);
//            this.unFollowee = request.getUnFollowee();
            sendSuccessMessage();

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }


    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(UnfollowTask.SUCCESS_KEY, response.isSuccess());
        msgBundle.putString(UnfollowTask.MESSAGE_KEY, response.getMessage());
    }


    private ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }
        return serverFacade;
    }
}
