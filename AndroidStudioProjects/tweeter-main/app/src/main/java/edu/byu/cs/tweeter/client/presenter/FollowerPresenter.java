package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowerService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements UserService.GetUserObserver, FollowerService.GetFollowersObserver{

    private View view;
    private User user;

    private User lastFollower;
    private boolean hasMorePages; // anytime there is any paging, we put this in the presenter
    private boolean isLoading = false;

    private static final int PAGE_SIZE = 10;


    public interface View {

        void showInfoMessage(String message);
        void showErrorMessage(String message);

        void openMainView(User user);

        void startingLoading();

        void endingLoading();

        void addItems(List<User> followers);
    }

    public FollowerPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }


    public void getUser(AuthToken authToken, String alias) {

        var userService = new UserService();
        userService.getUser(authToken, alias, this);
        view.showInfoMessage("Getting user's profile...");

    }


    @Override
    public void getUserSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.showErrorMessage(message);

    }

    /**
     * Causes the Adapter to display a loading footer and make a request to get more following
     * data.
     */
    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.startingLoading();

            var FollowerService = new FollowerService();
            FollowerService.getFollowers(Cache.getInstance().getCurrUserAuthToken(), user
                    ,PAGE_SIZE, lastFollower, this);
        }
    }


    @Override
    public void getFollowersSuccess(List<User> followers, boolean hasMorePages) {
        isLoading = false;
        lastFollower = (followers.size () > 0 ? followers.get (followers.size() - 1) : null);
        this.hasMorePages = hasMorePages;
        view.endingLoading();
        view.addItems(followers);
    }

    @Override
    public void getFollowersFailed(String message) {
        isLoading = false;
        view.endingLoading();
        view.showErrorMessage(message);
    }

    @Override
    public void handleException(Exception exception) {

    }

    public boolean getIsLoading() {
        return isLoading;
    }

public boolean getHasMorePages(){
        return hasMorePages;
}




}
