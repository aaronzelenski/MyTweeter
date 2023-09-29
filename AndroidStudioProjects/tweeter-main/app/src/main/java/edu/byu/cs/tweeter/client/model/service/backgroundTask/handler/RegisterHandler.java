package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.RegisterService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends Handler {

    private final RegisterService.RegisterObserver observer;


    public RegisterHandler(RegisterService.RegisterObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        boolean success = bundle.getBoolean(LoginTask.SUCCESS_KEY);
        if (success) {
            User user = (User) bundle.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(LoginTask.AUTH_TOKEN_KEY);
            observer.registerSucceeded(user, authToken);
        } else if (bundle.containsKey(LoginTask.MESSAGE_KEY)) {
            String errorMessage = bundle.getString(LoginTask.MESSAGE_KEY);
            observer.registerFailed(errorMessage);
        } else if (bundle.containsKey(LoginTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) bundle.getSerializable(LoginTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }


    }
}
