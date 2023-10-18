package edu.byu.cs.tweeter.client.model.service;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends SuperService {

    public FollowService() {}

    // ------------------- GetFollowers ------------------- //

    public interface GetFollowersObserver {
        void getFollowersSuccess(List<User> followers, boolean hasMorePages);
        void getFollowersFailed(String message);
        void handleException(Exception exception);
    }
    public void getFollowers(AuthToken authToken, User user, int pageSize, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,
                user, pageSize, lastFollower, new GetFollowersHandler(observer));
        executeTask(getFollowersTask);
    }
    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFollowingObserver observer) {
        GetFollowingTask followingTask = getGetFollowingTask(authToken, targetUser, limit, lastFollowee, observer);
        getBackgroundTaskUtils(followingTask);
    }


    public interface GetFollowingObserver {
        void getFollowingSuccess(List<User> followees, boolean hasMorePages);
        void handleFailure(String message);
        void handleException(Exception exception);
    }


    public GetFollowingTask getGetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFollowingObserver observer) {
        return new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFollowingHandler(observer));
    }
}
