package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;

public class PostStatusHandler extends HandlerGeneral {

    private StatusService.PostStatusObserver observer;

    public PostStatusHandler(StatusService.PostStatusObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        observer.postStatusSucceeded("Successfully Posted!");
    }

    @Override
    protected void handleFailure(String message) {
        observer.postStatusFailed(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.postStatusFailed("Failed to post status because of exception: " + ex.getMessage());
    }
}
