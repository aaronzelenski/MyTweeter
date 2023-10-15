package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;

public class FollowHandler extends HandlerGeneral {

    private StatusService.GetMainActivityObserver observer;

    public FollowHandler(StatusService.GetMainActivityObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        observer.followTaskSucceeded(true);
    }

    @Override
    protected void handleFailure(String message) {
        observer.followTaskFailed(message);
    }
}
