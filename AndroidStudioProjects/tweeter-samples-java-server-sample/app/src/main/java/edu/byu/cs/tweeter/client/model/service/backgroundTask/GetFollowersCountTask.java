package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends AuthenticatedTask {
    private static final String LOG_TAG = "GetFollowersCountTask";
    public static final String COUNT_KEY = "count";

    /**
     * Auth token for logged-in user.
     */
    /**
     * The user whose follower count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;
    private String alias;
    /**
     * Message handler that will receive task results.
     */

    private ServerFacade serverFacade;
    private GetFollowersCountResponse response;

    public GetFollowersCountTask(StatusService service, AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
    }

    @Override
    protected void runTask() {
        try {

            alias = targetUser.getAlias();

            GetFollowersCountRequest request = new GetFollowersCountRequest(alias, authToken);
            response = getServerFacade().getFollowersCount(request, FollowService.GET_FOLLOWERS_COUNT_URL_PATH);

            if(response.isSuccess()) {
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
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
        msgBundle.putInt(COUNT_KEY, response.getFollowersCount());
    }


}
