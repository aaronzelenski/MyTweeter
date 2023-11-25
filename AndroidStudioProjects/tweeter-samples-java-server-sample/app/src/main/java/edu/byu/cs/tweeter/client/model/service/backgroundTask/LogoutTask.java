package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import static edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask.AUTH_TOKEN_KEY;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.service.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {
    private static final String LOG_TAG = "LogoutTask";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private ServerFacade serverFacade;

    public LogoutTask(UserService userService, AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }


    @Override
    protected void runTask() {
        try {

            LogoutRequest request = new LogoutRequest(authToken);
            LogoutResponse response = getServerFacade().logout(request, UserService.LOGOUT_URL_PATH);
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


    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(LoginTask.AUTH_TOKEN_KEY, this.authToken);
    }



    private ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }
        return serverFacade;
    }
}
