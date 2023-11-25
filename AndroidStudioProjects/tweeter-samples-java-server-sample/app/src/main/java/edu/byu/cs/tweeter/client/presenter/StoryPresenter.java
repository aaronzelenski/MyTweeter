package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements UserService.GetUserObserver, StatusService.GetStoryObserver{

    private View view;
    private User user;
    private Status lastStatus;
    private boolean hasMorePages;
    boolean isLoading = false;
    private static final int PAGE_SIZE = 10;


    public StoryPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }



    public interface View{
        void showInfoMessage(String message);
        void showErrorMessage(String message);
        void startingLoading();
        void endingLoading();
        void openMainView(User user);
        void addItems(List<Status> statuses);
    }



    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.startingLoading();

            StatusService statusService = new StatusService();
            statusService.getStatus(Cache.getInstance().getCurrUserAuthToken(), user,
                    PAGE_SIZE, lastStatus, this);
        }
    }


    public void getUser(AuthToken authToken, String username){
        UserService userService = new UserService();
        userService.getUser(authToken, username, this);
        view.showInfoMessage("Getting user's profile...");
    }


    public boolean getIsLoading() {
        return isLoading;
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }




// methods overide from the StatusService.GetStoryObserver
    @Override
    public void getStorySucceeded(List<Status> statuses, boolean hasMorePages) {
        isLoading = false;
        lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
        this.hasMorePages = hasMorePages;
        view.endingLoading();
        view.addItems(statuses);
    }

    @Override
    public void getStoryFailed(String message) {
        view.showErrorMessage(message);
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

}
