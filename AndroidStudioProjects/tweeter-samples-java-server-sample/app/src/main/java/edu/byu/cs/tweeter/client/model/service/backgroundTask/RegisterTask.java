package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.service.response.RegisterResponse;

public class RegisterTask extends BackgroundTask{

    private static final String LOG_TAG = "RegisterTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private ServerFacade serverFacade;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String imageUrl;

    protected User user;
    protected AuthToken authToken;



    public RegisterTask(UserService service, String firstName, String lastName, String username, String password, String imageUrl, Handler messageHandler) {
        super(messageHandler);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.imageUrl = imageUrl;
    }


    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }

    @Override
    protected void runTask() {
        try {
            RegisterRequest request = new RegisterRequest(firstName, lastName, username, password, imageUrl);
            RegisterResponse response = getServerFacade().register(request, UserService.REGISTER_URL_PATH);
            if(response.isSuccess()) {
                this.user = response.getUser();
                this.authToken = response.getAuthToken();
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
}
