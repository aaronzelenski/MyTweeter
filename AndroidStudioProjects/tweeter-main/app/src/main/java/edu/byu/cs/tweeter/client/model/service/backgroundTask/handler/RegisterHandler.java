package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends HandlerGeneral {

    private UserService.RegisterObserver observer;
    public RegisterHandler(UserService.RegisterObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }
    @Override
    protected void handleSuccess(Message msg) {
        Bundle bundle = msg.getData();
        User registeredUser = (User) bundle.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) bundle.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        observer.registerSucceeded(registeredUser, authToken);

    }

    @Override
    protected void handleFailure(String message) {
        observer.registerFailed(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.registerFailed("Failed to register because of exception: " + ex.getMessage());

    }




}
