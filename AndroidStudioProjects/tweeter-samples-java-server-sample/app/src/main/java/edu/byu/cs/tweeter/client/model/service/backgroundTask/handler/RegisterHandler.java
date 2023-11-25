package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends Handler {

    private final UserService.RegisterObserver observer;


    public RegisterHandler(UserService.RegisterObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        boolean success = bundle.getBoolean(LoginTask.SUCCESS_KEY);
        if (success) {
            User user = (User) bundle.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(user);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.registerSucceeded(user, authToken);
        } else if (bundle.containsKey(LoginTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(LoginTask.MESSAGE_KEY);
            observer.registerFailed(errorMessage);
        } else if (bundle.containsKey(LoginTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(LoginTask.EXCEPTION_KEY);
            observer.registerFailed("Failed because of exception: " + ex.getMessage());
        }


    }
}
