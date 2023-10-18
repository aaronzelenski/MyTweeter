package edu.byu.cs.tweeter.client.presenter;
import java.util.List;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User> implements UserService.GetUserObserver, FollowService.GetFollowersObserver {

    private final PagedView<User> view;
    private final User user;
    private User lastFollower;

    public FollowerPresenter(PagedView<User> view, User user) {
        this.view = view;
        this.user = user;
    }


    public void getUser(AuthToken authToken, String clickable) {

        var userService = new UserService();
        userService.getUser(authToken, clickable, this);
        view.showInfoMessage("Getting user's profile...");

    }


    @Override
    public void getUserSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void handleFailure(String message) {
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

            var FollowerService = new FollowService();
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




}
