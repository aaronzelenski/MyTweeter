package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;

public class IsFollowerHandler extends HandlerGeneral {

    private StatusService.isFollowerObserver observer;

    public IsFollowerHandler(StatusService.isFollowerObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        observer.isFollowerSucceeded(msg);
    }

    @Override
    protected void handleFailure(String message) {
        observer.isFollowerFailed(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.isFollowerFailed("Failed to determine following relationship because of exception: " + ex.getMessage());
    }
}
