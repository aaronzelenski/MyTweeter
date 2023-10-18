package edu.byu.cs.tweeter.client.model.service;
import android.os.Message;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.*;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends SuperService {



    // ------------------- GetFeed ------------------- //
    public interface getFeedObserver extends ServiceObserver<Status> {
        void getFeedSucceeded(List<Status> statuses, boolean hasMorePages);
        void getFeedFailed(String message);
    }


    public void getFeed(AuthToken authtoken, User user, int pageSize, Status lastStatus, getFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authtoken,
                user, pageSize, lastStatus, new GetFeedHandler(observer));
        executeTask(getFeedTask);
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
        executeTask(getStoryTask);

    }

    // ------------------- Main Activity ------------------- //
    public void GetMainActivity(GetMainActivityObserver observer){ // some other parameters will be put here soon
    }

    public interface GetMainActivityObserver {
        void followTaskSucceeded(boolean isFollowing);
        void followTaskFailed(String message);

        void unfollowTaskSucceeded(boolean isFollowing);
        void unfollowTaskFailed(String message);

    };

    public void followTask(AuthToken authToken, User selectedUser,
                           GetMainActivityObserver getMainActivityObserver){

        FollowTask followTask = new FollowTask(authToken,
                selectedUser, new FollowHandler(getMainActivityObserver));
        executeTask(followTask);

    }
    public void unfollowTask(AuthToken authToken, User selectedUser,
                             GetMainActivityObserver getMainActivityObserver){
        UnfollowTask unfollowTask = new UnfollowTask(authToken,
                selectedUser, new UnfollowHandler(getMainActivityObserver));
        executeTask(unfollowTask);

    }


    // ------------------- PostStatus ------------------- //
    public interface PostStatusObserver {
        void postStatusSucceeded(String message);
        void postStatusFailed(String message);
    }



    public void postTask(String post, User user, long currentTimeMillis,
                         List<String> parseURLs, List<String> parseMentions, PostStatusObserver observer){
        Status newStatus = new Status(post, user, currentTimeMillis,parseURLs, parseMentions);
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }

    // ----------------------- Logout Task ----------------------------

    public interface LogoutObserver {
        void logoutSucceeded();
        void logoutFailed(String message);
    }

    public void logoutTask(AuthToken authToken, LogoutObserver observer){
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        executeTask(logoutTask);
    }


    // ------------------ get Following -------------------

    public interface followingCountObserver{
        void updateFollowingCountSucceeded(Message msg);
        void updateFollowingCountFailed(String message);
    }

    public void getFollowingCountTask(AuthToken authToken, User selectedUser, followingCountObserver observer){
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                selectedUser, new GetFollowingCountHandler(observer));
        executeTask(followingCountTask);

    }


    // ------------------ get Followers -------------------
    public interface followerCountObserver{
        void updateFollowerCountSucceeded(Message msg);
        void updateFollowerCountFailed(String message);
    }

    public void getFollowersCountTask(AuthToken authToken, User selectedUser, followerCountObserver observer){
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken,
                selectedUser, new GetFollowersCountHandler(observer));
        executeTask(followersCountTask);

    }


    // --------------------- isFollower ---------------------------
    public interface isFollowerObserver{

        void isFollowerSucceeded(Message msg);
        void isFollowerFailed(String message);
    }

    public void isFollowerTask(AuthToken authToken, User currUser, User selectedUser, isFollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                currUser, selectedUser, new IsFollowerHandler(observer));
        executeTask(isFollowerTask);
    }
}
