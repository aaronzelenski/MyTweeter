package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;
import android.os.Looper;
import android.os.Message;
import edu.byu.cs.tweeter.client.model.service.StatusService;

public class GetFollowersCountHandler extends HandlerGeneral {

    private StatusService.followerCountObserver observer;

    public GetFollowersCountHandler(StatusService.followerCountObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        observer.updateFollowerCountSucceeded(msg);
    }

    @Override
    protected void handleFailure(String message) {
        observer.updateFollowerCountFailed(message);
    }
}