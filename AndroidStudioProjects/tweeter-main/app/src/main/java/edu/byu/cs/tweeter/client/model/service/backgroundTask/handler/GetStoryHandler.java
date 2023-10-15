package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryHandler extends HandlerGeneral {

    private StatusService.getStatusObserver observer;

    public GetStoryHandler(StatusService.getStatusObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        Bundle bundle = msg.getData();
        List<Status> statuses = (List<Status>) bundle.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = bundle.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
        observer.getStatusSucceeded(statuses, hasMorePages);
    }

    @Override
    protected void handleFailure(String message) {
        observer.getStatusFailed(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.getStatusFailed("Failed to get story because of exception: " + ex.getMessage());
    }
}
