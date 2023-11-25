package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter implements UserService.GetUserObserver, FollowService.GetFollowingObserver{

    private static final int PAGE_SIZE = 10;

    private View view;
    private User user;

    private User lastFollowee;
    private boolean hasMorePages;
    private boolean isLoading = false;




    public interface View {
        void showInfoMessage(String message);
        void showErrorMessage(String message);
        void openMainView(User user);
        void startingLoading();
        void endingLoading();
        void addItems(List<User> newFollowees);

    }

    public FollowingPresenter(View view, User user) {
        this.view = view;
        this.user = user;
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
            followService.getFollowing(Cache.getInstance().getCurrUserAuthToken(), user,
                    PAGE_SIZE, lastFollowee, this);
        }
    }



    // these are methods overridden from the GetUserObserver interface
    @Override
    public void getUserSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.showErrorMessage(message);
    }






// these method are overridden from the GetFollowingObserver interface
    @Override
    public void getFollowingSucceeded(List<User> followees, boolean hasMorePages) {
        isLoading = false;
        lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
        this.hasMorePages = hasMorePages;
        view.endingLoading();
        view.addItems(followees);

    }

    @Override
    public void getFollowingFailed(String message) {
        isLoading = false;
        view.endingLoading();
        view.showErrorMessage(message);
    }


    public boolean getIsLoading() {
        return isLoading;
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }
}
