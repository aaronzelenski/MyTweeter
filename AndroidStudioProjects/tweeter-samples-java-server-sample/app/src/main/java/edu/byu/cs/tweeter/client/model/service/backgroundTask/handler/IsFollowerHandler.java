package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;

public class IsFollowerHandler extends Handler {

    private StatusService.isFollowerObserver observer;

    public IsFollowerHandler(StatusService.isFollowerObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
        if (success) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

//            // If logged in user if a follower of the selected user, display the follow button as "following"
//            if (isFollower) {
//                followButton.setText(R.string.following);
//                followButton.setBackgroundColor(getResources().getColor(R.color.white));
//                followButton.setTextColor(getResources().getColor(R.color.lightGray));
//            } else {
//                followButton.setText(R.string.follow);
//                followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//            }
                observer.isFollowerSucceeded(isFollower);
        } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
            observer.isFollowerFailed("Failed to determine following relationship: " + message);
        } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
            observer.isFollowerFailed("Failed to determine following relationship because of exception: " + ex.getMessage());
        }
    }
}
