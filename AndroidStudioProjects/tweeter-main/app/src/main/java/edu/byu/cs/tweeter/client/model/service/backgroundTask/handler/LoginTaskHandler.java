package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

//import edu.byu.cs.tweeter.client.model.service.*;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Handles messages from the background task indicating that the task is done, by invoking
 * methods on the observer.
 */
public class LoginTaskHandler extends HandlerGeneral {

    private final UserService.LoginObserver observer;

    public LoginTaskHandler(UserService.LoginObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        Bundle bundle = msg.getData();
        User user = (User) bundle.getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) bundle.getSerializable(LoginTask.AUTH_TOKEN_KEY);
        observer.handleSuccess(user, authToken);
    }

    @Override
    protected void handleFailure(String message) {
        observer.handleFailure(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.handleException(ex);
    }
}
