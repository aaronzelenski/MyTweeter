package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements UserService.GetUserObserver, FollowService.GetFollowerObserver {

    private View view;
    private User user;
    private User lastFollower;
    private static final int PAGE_SIZE = 10;
    private boolean hasMorePages;
    private boolean isLoading = false;

    public FollowerPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }




    public interface View{
        void showInfoMessage(String message);
        void showErrorMessage(String message);
        void openMainView(User user);
        void startingLoading();
        void endingLoading();
        void addItems(List<User> followers);
    }


    public void getUser(AuthToken authToken, String username){
        UserService userService = new UserService();
        userService.getUser(authToken, username, this);
        view.showInfoMessage("Getting user's profile...");
    }


    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.startingLoading();

            FollowService followService = new FollowService();
            followService.getFollower(Cache.getInstance().getCurrUserAuthToken(), user,
                    PAGE_SIZE, lastFollower, this);
        }
    }




    public boolean getIsLoading() {
        return isLoading;
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }




// these methods are overide from the UserService.GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.showErrorMessage(message);
    }




// these method are override from FollowService.GetFollowerObserver
    @Override
    public void getFollowerSucceeded(List<User> followers, boolean hasMorePages) {
        isLoading = false;
        lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
        this.hasMorePages = hasMorePages;
        view.endingLoading();
        view.addItems(followers);
    }

    @Override
    public void getFollowerFailed(String message) {
        view.showErrorMessage(message);
    }
}
