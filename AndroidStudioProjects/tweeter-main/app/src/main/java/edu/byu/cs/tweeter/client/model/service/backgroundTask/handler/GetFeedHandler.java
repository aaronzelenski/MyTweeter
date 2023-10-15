package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedHandler extends HandlerGeneral {
    private StatusService.getFeedObserver observer;
    public GetFeedHandler(StatusService.getFeedObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }
    @Override
    protected void handleSuccess(Message msg) {
        List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.getFeedSucceeded(statuses, hasMorePages);
    }

    @Override
    protected void handleFailure(String message) {
        observer.getFeedFailed(message);
    }
}
