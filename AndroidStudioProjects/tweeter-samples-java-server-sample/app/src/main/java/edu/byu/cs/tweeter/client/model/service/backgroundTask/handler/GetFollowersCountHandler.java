package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

// GetFollowersCountHandler

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;

public class GetFollowersCountHandler extends Handler {

    private final StatusService.getFollowersCountObserver observer;

    public GetFollowersCountHandler(StatusService.getFollowersCountObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
        if (success) {
            int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            observer.getFollowersCountSucceeded(count);
        } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
            observer.getFollowersCountFailed("Failed to get followers count: " + message);
        } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
            observer.getFollowersCountFailed("Failed to get followers count because of exception: " + ex.getMessage());
        }
    }
}


//followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
