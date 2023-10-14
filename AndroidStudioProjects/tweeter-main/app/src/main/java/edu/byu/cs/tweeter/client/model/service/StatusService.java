package edu.byu.cs.tweeter.client.model.service;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
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
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
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
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
                assert statuses != null;
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
        void followTaskSucceeded(boolean isFollowing); // im not sure we want to return a boolean here...
        void followTaskFailed(String message);

        void unfollowTaskSucceeded(boolean isFollowing);
        void unfollowTaskFailed(String message);

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
                observer.followTaskSucceeded(true);
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.followTaskFailed("Failed to follow: " + message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.followTaskFailed("Failed to follow because of exception: " + ex.getMessage());
            }
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
                observer.unfollowTaskSucceeded(true);
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.unfollowTaskFailed("Failed to unfollow: " + message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.unfollowTaskFailed("Failed to unfollow because of exception: " + ex.getMessage());
            }
        }
    }


    // ------------------- PostStatus ------------------- //


    public interface PostStatusObserver {
        void postStatusSucceeded(String message); // not sure what we want to return here yet...
        void postStatusFailed(String message);
    }



    public void postTask(String post, User user, long currentTimeMillis,
                         List<String> parseURLs, List<String> parseMentions, PostStatusObserver observer){
        Status newStatus = new Status(post, user, currentTimeMillis, parseURLs(post), parseMentions(post));
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new PostStatusHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(statusTask);
    }

    private class PostStatusHandler extends Handler {

        private PostStatusObserver observer;

        public PostStatusHandler(PostStatusObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(PostStatusTask.SUCCESS_KEY);
            if (success) {
                observer.postStatusSucceeded("Successfully Posted!");
            } else if (msg.getData().containsKey(PostStatusTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(PostStatusTask.MESSAGE_KEY);
                observer.postStatusFailed("Failed to post status: " + message);
            } else if (msg.getData().containsKey(PostStatusTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(PostStatusTask.EXCEPTION_KEY);
                observer.postStatusFailed("Failed to post status because of exception: " + ex.getMessage());
            }
        }
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }


    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }


    // ----------------------- Logout Task ----------------------------


    public interface LogoutObserver {
        void logoutSucceeded();
        void logoutFailed(String message);
    }


    public void logoutTask(AuthToken authToken, LogoutObserver observer){
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }



    private class LogoutHandler extends Handler {

        private LogoutObserver observer;

        public LogoutHandler(LogoutObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
            if (success) {
                observer.logoutSucceeded();
            } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LogoutTask.MESSAGE_KEY);
                observer.logoutFailed("Failed to logout: " + message);
                //Toast.makeText(MainActivity.this, "Failed to logout: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                observer.logoutFailed("Failed to logout because of exception: " + ex);
                //Toast.makeText(MainActivity.this, "Failed to logout because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    // ------------------ get Following -------------------



    public interface followingCountObserver{
        void updateFollowingCountSucceeded(Message msg);
        void updateFollowingCountFailed(String message);
    }


    public void getFollowingCountTask(AuthToken authToken, User selectedUser, followingCountObserver observer){

        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                selectedUser, new GetFollowingCountHandler(observer));
        executor.execute(followingCountTask);

    }




    private class GetFollowingCountHandler extends Handler {

        private followingCountObserver observer;

        public GetFollowingCountHandler(followingCountObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                observer.updateFollowingCountSucceeded(msg);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.updateFollowingCountFailed("Failed to get following count: " + message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.updateFollowingCountFailed("Failed to get following count because of exception: " + ex.getMessage());
            }
        }
    }





    // ------------------ get Followers -------------------



    public interface followerCountObserver{
        void updateFollowerCountSucceeded(Message msg);
        void updateFollowerCountFailed(String message);
    }


    public void getFollowersCountTask(AuthToken authToken, User selectedUser, followerCountObserver observer){

        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken,
                selectedUser, new GetFollowersCountHandler(observer));
        executor.execute(followersCountTask);

    }


    private class GetFollowersCountHandler extends Handler {

        private followerCountObserver observer;

        public GetFollowersCountHandler(followerCountObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                observer.updateFollowerCountSucceeded(msg);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.updateFollowerCountFailed("Failed to get followers count: " + message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.updateFollowerCountFailed("Failed to get followers count because of exception: " + ex.getMessage());
            }
        }
    }








    // --------------------- isFollower ---------------------------

    public interface isFollowerObserver{

        void isFollowerSucceeded(Message msg);
        void isFollowerFailed(String message);
    }

    public void isFollowerTask(AuthToken authToken, User currUser, User selectedUser, isFollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                currUser, selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);

    }

    private class IsFollowerHandler extends Handler {

        private isFollowerObserver observer;

        public IsFollowerHandler(isFollowerObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                observer.isFollowerSucceeded(msg);
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.isFollowerFailed("Failed to determine following relationship: " + message);
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.isFollowerFailed("Failed to determine following relationship because of exception: " + ex.getMessage());
            }
        }
    }




}
