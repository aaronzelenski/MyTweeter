package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends BackgroundTask {
    private static final String LOG_TAG = "PostStatusTask";
    public static final String STATUS_KEY = "status";
    public static final String AUTH_TOKEN_KEY = "auth-token";


    protected AuthToken authToken;
    private Status status;

    private ServerFacade serverFacade;

    public PostStatusTask(StatusService statusService, AuthToken authToken, Status status, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.status = status;
    }

    @Override
    protected void runTask() {
        try{
            PostStatusRequest request = new PostStatusRequest(authToken, status);
            PostStatusResponse response = getServerFacade().postStatus(request, StatusService.POST_STATUS_URL_PATH);

            if(response.isSuccess()) {
                this.status = response.getStatus();
                sendSuccessMessage();
            } else {
                throw new Exception(response.getMessage());
            }

        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }


    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(STATUS_KEY, this.status);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, this.authToken);
    }


    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }
        return serverFacade;
    }



}
