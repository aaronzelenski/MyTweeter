package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;

public class LogoutHandler extends Handler {

    private final UserService.LogoutObserver observer;

    public LogoutHandler(UserService.LogoutObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }
    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
        if (success) {
            observer.logoutSucceeded();
        } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(LogoutTask.MESSAGE_KEY);
            observer.logoutFailed("Failed to logout: " + message);
        } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
            observer.logoutFailed("Failed to logout because of exception: " + ex.getMessage());
        }
    }
}
