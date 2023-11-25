package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public static final String POST_STATUS_URL_PATH = "/poststatus";
    public static final String GET_FEED_URL_PATH = "/getfeed";
    public static final String IS_FOLLOWER_URL = "/isfollower";
    public static final String TO_FOLLOW_URL = "/tofollow";
    public static final String GET_STORY_URL_PATH = "/getstory";
    public static final String GET_FOLLOWING_COUNT_URL_PATH = "/getfollowingcount";


    public StatusService() {}

    public interface GetFeedObserver{
        void GetFeedSucceeded(List<Status> statuses, boolean hasMorePages);
        void getFeedFailed(String message);
    }



    public void getFeed(AuthToken authtoken, User user, int pageSize, Status lastStatus, GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(this, authtoken,
                user, pageSize, lastStatus, new GetFeedHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);
    }


    public interface GetStoryObserver{
        void getStorySucceeded(List<Status> statuses, boolean hasMorePages);
        void getStoryFailed(String message);
    }

    public void getStatus(AuthToken authtoken, User user,
                          int pageSize, Status lastStatus, GetStoryObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(this, authtoken,
                user, pageSize, lastStatus, new GetStoryHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }



    public interface postStatusObserver{
        void postStatusSucceeded(String message);
        void postStatusFailed(String message);
    }

    public void postStatus(AuthToken authToken, Status newStatus, postStatusObserver observer){

        PostStatusTask statusTask = new PostStatusTask(this, authToken,
                    newStatus, new PostStatusHandler(observer));

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(statusTask);
    }



    public interface getFollowersCountObserver{
        void getFollowersCountSucceeded(int followersCount);
        void getFollowersCountFailed(String message);
    }

    public void getFollowersCountTask(AuthToken authToken, User selectedUser, getFollowersCountObserver observer){
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(this, authToken,
                selectedUser, new GetFollowersCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followersCountTask);
    }


    public interface getFollowingCountObserver{
        void getFollowingCountSucceeded(int followingCount);
        void getFollowingCountFailed(String message);
    }

    public void getFollowingCountTask(AuthToken authToken, User selectedUser, getFollowingCountObserver observer){
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(this, authToken,
                selectedUser, new GetFollowingCountHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followingCountTask);
    }



    public interface isFollowerObserver{
        void isFollowerSucceeded(boolean isFollower);
        void isFollowerFailed(String message);
    }

    public void isFollowerTask(AuthToken authToken, User currUser, User selectedUser, isFollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(this, authToken,
                currUser, selectedUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }


    public interface unfollowObserver{
        void unfollowSucceeded(boolean isFollowing);
        void unfollowFailed(String message);

        void updateSelectedUserFollowingAndFollowers();

        void updateFollowButton(boolean isFollowing);
    }

    public void unfollowTask(AuthToken authToken, User unFollower, User unFollowee,
                             unfollowObserver unfollowObserver){
        UnfollowTask unfollowTask = new UnfollowTask(authToken,unFollower,
                unFollowee, new UnfollowHandler(unfollowObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    public interface followObserver{
        void followSucceeded(boolean isFollowing);
        void followFailed(String message);
        void updateSelectedUserFollowingAndFollowers();
        void updateFollowButton(boolean isFollowing);
    }


    public void followTask(AuthToken authToken, User follower, User followee,
                           followObserver followObserver){

        FollowTask followTask = new FollowTask(this, authToken,follower,
                followee, new FollowHandler(followObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);

    }


}
