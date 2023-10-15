package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;

public class GetFollowingCountHandler extends HandlerGeneral {

    private StatusService.followingCountObserver observer;

    public GetFollowingCountHandler(StatusService.followingCountObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        observer.updateFollowingCountSucceeded(msg);
    }

    @Override
    protected void handleFailure(String message) {
        observer.updateFollowingCountFailed(message);
    }
}