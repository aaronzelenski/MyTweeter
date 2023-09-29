package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerService {

    // ------------------- GetFollowers ------------------- //

    // 48:00 on video

    public interface GetFollowersObserver {
        void getFollowersSuccess(List<User> followers, boolean hasMorePages);
        void getFollowersFailed(String message);
        void handleException(Exception exception);
    }



    public void getFollowers(AuthToken authToken, User user, int pageSize, User lastFollower, GetFollowersObserver observer) {

        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,
                user, pageSize, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }



    private class GetFollowersHandler extends Handler {

        private GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {


            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
                observer.getFollowersSuccess(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailed("Failed to get followers: " + message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersFailed("Failed to get followers because of exception: " + ex.getMessage());
            }
        }
    }









}
