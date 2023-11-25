package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public static final String FOLLOW_URL_PATH = "/getfollowing";
    public static final String FOLLOWER_URL_PATH = "/getfollower";
    public static final String GET_FOLLOWERS_COUNT_URL_PATH = "/getfollowerscount";

    public static final String TO_UNFOLLOW_URL = "/tounfollow";




    public interface GetFollowingObserver{
        void getFollowingSucceeded(List<User> followees, boolean hasMorePages);
        void getFollowingFailed(String message);
    }
    public void getFollowing(AuthToken authToken, User user, int pageSize,
                             User lastFollowee, GetFollowingObserver observer){


        GetFollowingTask getFollowingTask = new GetFollowingTask(this, authToken,
                user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);

    }



    public interface GetFollowerObserver{
        void getFollowerSucceeded(List<User> followers, boolean hasMorePages);
        void getFollowerFailed(String message);
    }

    public void getFollower(AuthToken authToken, User user, int pageSize,
                             User lastFollowee, GetFollowerObserver observer){

        GetFollowersTask getFollowersTask = new GetFollowersTask(this, authToken,
                user, pageSize, lastFollowee, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);


    }


}
