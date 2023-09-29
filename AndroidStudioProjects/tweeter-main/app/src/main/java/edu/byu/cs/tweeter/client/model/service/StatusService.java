package edu.byu.cs.tweeter.client.model.service;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.story.StoryFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {


    public interface getFeedObserver {
        void getFeedSucceeded(List<Status> statuses, boolean hasMorePages); // what do we want to return? I believe we want to return a user object

        void getFeedFailed(String message);
    }


    public void getFeed(AuthToken authtoken, User user, int pageSize, Status lastStatus, getFeedObserver observer) {

        GetFeedTask getFeedTask = new GetFeedTask(authtoken,
                user, pageSize, lastStatus, new GetFeedHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);


    }

    private class GetFeedHandler extends Handler {

        private getFeedObserver observer;

        public GetFeedHandler(getFeedObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            boolean success = msg.getData().getBoolean(GetFeedTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.STATUSES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
                Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
                observer.getFeedSucceeded(statuses, hasMorePages);
            } else if (msg.getData().containsKey(GetFeedTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFeedTask.MESSAGE_KEY);
                observer.getFeedFailed("Failed to get feed: " + message);
            } else if (msg.getData().containsKey(GetFeedTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFeedTask.EXCEPTION_KEY);
                observer.getFeedFailed("Failed to get feed because of exception: " + ex.getMessage());
            }
        }
    }


    // ------------------- PostStatus ------------------- //


    public interface getStatusObserver {
        void getStatusSucceeded(List<Status> statuses, boolean hasMorePages);
        void getStatusFailed(String message);

    }

    public void getStatus(AuthToken authtoken, User user,
                          int pageSize, Status lastStatus, getStatusObserver observer) {


        GetStoryTask getStoryTask = new GetStoryTask(authtoken,
                user, pageSize, lastStatus, new GetStoryHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);

    }



    private class GetStoryHandler extends Handler {

        private getStatusObserver observer;

        public GetStoryHandler(getStatusObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {


            boolean success = msg.getData().getBoolean(GetStoryTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.STATUSES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
                Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
                observer.getStatusSucceeded(statuses, hasMorePages);
            } else if (msg.getData().containsKey(GetStoryTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetStoryTask.MESSAGE_KEY);
                observer.getStatusFailed("Failed to get story: " + message);
            } else if (msg.getData().containsKey(GetStoryTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetStoryTask.EXCEPTION_KEY);
                observer.getStatusFailed("Failed to get story because of exception: " + ex.getMessage());
            }
        }
    }



    // ------------------- Main Activity ------------------- //


    public void GetMainActivity(GetMainActivityObserver observer){ // some other parameters will be put here soon

    }



    public interface GetMainActivityObserver {
        void followSucceeded(boolean isFollowing);
        void followFailed(String message);
    };



    public void followTask(AuthToken authToken, User selectedUser,
                           GetMainActivityObserver getMainActivityObserver){

        FollowTask followTask = new FollowTask(authToken,
                selectedUser, new FollowHandler(getMainActivityObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);

    }



    public void unfollowTask(AuthToken authToken, User selectedUser,
                             GetMainActivityObserver getMainActivityObserver){

        UnfollowTask unfollowTask = new UnfollowTask(authToken,
                selectedUser, new UnfollowHandler(getMainActivityObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);

    }

    private class FollowHandler extends Handler {

        private GetMainActivityObserver observer;
        public FollowHandler(GetMainActivityObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                updateSelectedUserFollowingAndFollowers();
                updateFollowButton(false);
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                Toast.makeText(MainActivity.this, "Failed to follow: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                Toast.makeText(MainActivity.this, "Failed to follow because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            followButton.setEnabled(true);
        }
    }

    // UnfollowHandler

    private class UnfollowHandler extends Handler {

        private GetMainActivityObserver observer;

        public UnfollowHandler(GetMainActivityObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                updateSelectedUserFollowingAndFollowers();
                updateFollowButton(true);
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                Toast.makeText(MainActivity.this, "Failed to unfollow: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                Toast.makeText(MainActivity.this, "Failed to unfollow because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            followButton.setEnabled(true);
        }
    }








}
