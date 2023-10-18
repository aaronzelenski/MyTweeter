package edu.byu.cs.tweeter.client.presenter;
import java.util.List;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
public class FeedPresenter extends PagedPresenter<Status> implements StatusService.getFeedObserver, UserService.GetUserObserver {

    private final PagedView<Status> view;
    private final User user;
    private Status lastStatus;


    public FeedPresenter(PagedView<Status> view, User user) {// constructor
        this.view = view;
        this.user = user;
    }


    public void getUser(AuthToken authToken, String clickable){
        var UserService = new UserService();
        UserService.getUser(authToken, clickable, this);
        view.showInfoMessage("Getting user's profile...");
    }


    /**
     * Causes the Adapter to display a loading footer and make a request to get more feed
     * data.
     */
    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.startingLoading();
            var feedService = new StatusService();
            feedService.getFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE,lastStatus,this);
        }
    }



    @Override
    public void getFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
        isLoading = false;
        view.endingLoading();
        lastStatus = (statuses.size () > 0 ? statuses.get (statuses.size() - 1) : null);
        this.hasMorePages = hasMorePages;
        view.addItems(statuses);

    }

    @Override
    public void getFeedFailed(String message) {
        isLoading = false;
        view.endingLoading();
        view.showErrorMessage(message);
    }

    @Override
    public void getUserSucceeded(User user) {
        view.hideInfoMessage();
        view.openMainView(user);
    }

    @Override
    public void handleFailure(String message) {
        view.showErrorMessage(message);

    }

}
