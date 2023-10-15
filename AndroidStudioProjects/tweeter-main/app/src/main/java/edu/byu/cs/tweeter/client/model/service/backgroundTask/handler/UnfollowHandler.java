package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;

public class UnfollowHandler extends HandlerGeneral {

    private StatusService.GetMainActivityObserver observer;

    public UnfollowHandler(StatusService.GetMainActivityObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        observer.unfollowTaskSucceeded(true);
    }

    @Override
    protected void handleFailure(String message) {
        observer.unfollowTaskFailed(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.unfollowTaskFailed("Failed to unfollow because of exception: " + ex.getMessage());
    }
}
