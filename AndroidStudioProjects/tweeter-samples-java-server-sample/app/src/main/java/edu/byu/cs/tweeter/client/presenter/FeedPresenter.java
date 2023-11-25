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

public class FeedPresenter implements UserService.GetUserObserver, StatusService.GetFeedObserver{

    View view;
    User user;
    private Status lastStatus;
    private boolean hasMorePages;
    boolean isLoading = false;
    private static final int PAGE_SIZE = 10;




    public FeedPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }


    public interface View{
        void showInfoMessage(String message);
        void showErrorMessage(String message);
        void openMainView(User user);
        void startingLoading();
        void endingLoading();
        void addItems(List<Status> statuses);
    }


    public void getUser(AuthToken authToken, String clickable){
        view.showInfoMessage("Getting user's profile...");
        UserService userService = new UserService();
        userService.getUser(authToken, clickable, this);
    }


    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            loadData();
        }
    }


    public boolean getIsLoading() {
        return isLoading;
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }



    protected void loadData() {
        view.startingLoading();
        StatusService feedService = new StatusService();
        feedService.getFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE,lastStatus,this);
    }




// override methods from UserService.GetUserObserver
    @Override
    public void getUserSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.showErrorMessage(message);
    }



// override methods from StatusService.GetFeedObserver
    @Override
    public void GetFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
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


}
