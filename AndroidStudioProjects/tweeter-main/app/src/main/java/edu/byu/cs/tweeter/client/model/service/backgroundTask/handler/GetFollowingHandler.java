package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import java.util.List;

//import edu.byu.cs.tweeter.client.model.service.*;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Handles messages from the background task indicating that the task is done, by invoking
 * methods on the observer.
 */
public class GetFollowingHandler extends HandlerGeneral {

    private final FollowService.GetFollowingObserver observer;

    public GetFollowingHandler(FollowService.GetFollowingObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.getFollowingSuccess(followees, hasMorePages);
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
