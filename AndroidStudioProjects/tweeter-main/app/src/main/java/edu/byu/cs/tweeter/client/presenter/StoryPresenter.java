package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> implements UserService.GetUserObserver, StatusService.getStatusObserver {

    private final PagedView<Status> view;
    private final User user;
    private Status lastStatus;



    public StoryPresenter(PagedView<Status> view, User user){
        this.view = view;
        this.user = user;
    }


    public void getUser(AuthToken authToken, String clickable){
        var userService = new UserService();
        userService.getUser(authToken, clickable, this);
        view.showInfoMessage("Getting user's profile...");
    }


    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.startingLoading();

            var statusService = new StatusService();
            statusService.getStatus(Cache.getInstance().getCurrUserAuthToken(),
                    user, PAGE_SIZE, lastStatus, this);

        }
    }


    @Override
    public void getUserSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void handleFailure(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void getStatusSucceeded(List<Status> statuses, boolean hasMorePages) {
        isLoading = false;
        lastStatus = (statuses.size() > 0 ? statuses.get(statuses. size() - 1) : null);
        this.hasMorePages = hasMorePages;
        view.endingLoading();
        view.addItems(statuses);
    }

    @Override
    public void getStatusFailed(String message) {
        isLoading = false;
        view.endingLoading();
        view.showErrorMessage(message);
    }



}
