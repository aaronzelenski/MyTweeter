package edu.byu.cs.tweeter.client.presenter;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter implements UserService.LogoutObserver, StatusService.postStatusObserver,
    StatusService.getFollowersCountObserver, StatusService.getFollowingCountObserver, StatusService.isFollowerObserver,
    StatusService.unfollowObserver, StatusService.followObserver{

    private View view;
    private User selectedUser;

    private String post;

//    public MainActivityPresenter(View view, User currentUser, User selectedUser){
//        this.view = view;
//        this.currentUser = currentUser;
//        this.selectedUser = selectedUser;
//    }

    public MainActivityPresenter(View view, User selectedUser){
        this.view = view;
        this.selectedUser = selectedUser;
    }
    public StatusService createService(){
        return new StatusService();
    }


    public interface View {
        void showInfoMessage(String message);
        void showErrorMessage(String message);
        void hideInfoMessage();
        void hideErrorMessage();
        void logoutUser();
        void postStatus();
        void showSuccessMessage(String message);

        void updateFollowersCountView(int followersCount);
        void updateFollowingCountView(int followingCount);

        void updateButtonView(boolean isFollowing);
        void updateSelectedUserFollowingAndFollowers();
    }


    public void logout(AuthToken authToken){
        view.showInfoMessage("Logging Out...");
        UserService userService = new UserService();
        userService.logout(authToken, this);
    }


    public void postStatus(AuthToken authToken, Status newStatus){
        view.showInfoMessage("Posting status...");
        StatusService statusService = new StatusService();
        statusService.postStatus(authToken, newStatus, this);
    }


    public void getFollowersCount(AuthToken authToken, User selectedUser){
        StatusService statusService = new StatusService();
        statusService.getFollowersCountTask(authToken, selectedUser, this);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser){
        StatusService statusService = new StatusService();
        statusService.getFollowingCountTask(authToken, selectedUser, this);
    }


    public void isFollower(AuthToken authToken, User currUser, User selectedUser){
        StatusService statusService = new StatusService();
        statusService.isFollowerTask(authToken, currUser, selectedUser,this);
    }

    public void unfollow(AuthToken authToken, User currentUser, User selectedUser){
        view.showInfoMessage("Removing " + selectedUser.getName() + "...");
        StatusService statusService = new StatusService();
        statusService.unfollowTask(authToken, currentUser, selectedUser, this);
    }

    public void follow(AuthToken authToken, User currentUser , User selectedUser){
        view.showInfoMessage("Following " + selectedUser.getName() + "...");
        StatusService statusService = new StatusService();
        statusService.followTask(authToken, currentUser, selectedUser, this); // user is not right
    }







    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                int index = findUrlEndIndex(word);
                word = word.substring(0, index);
                containedUrls.add(word);
            }
        }
        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);
                containedMentions.add(word);
            }
        }
        return containedMentions;
    }


    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }




    // Methods override from UserService.LogoutObserver

    @Override
    public void logoutSucceeded() {
        view.hideInfoMessage();
        view.hideErrorMessage();
        view.showInfoMessage("Successfully logged out");
        view.logoutUser();
    }

    @Override
    public void logoutFailed(String message) {
        view.showErrorMessage(message);
    }


// methods override from StatusService.postStatusObserver
    @Override
    public void postStatusSucceeded(String message) {
        view.showSuccessMessage("Successfully posted status");
    }

    @Override
    public void postStatusFailed(String message) {
        view.showErrorMessage(message);
    }



    @Override
    public void getFollowersCountSucceeded(int followersCount) {
        view.updateFollowersCountView(followersCount);
    }

    @Override
    public void getFollowersCountFailed(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void getFollowingCountSucceeded(int followingCount) {
        view.updateFollowingCountView(followingCount);
    }

    @Override
    public void getFollowingCountFailed(String message) {
        view.showErrorMessage(message);
    }


    @Override
    public void isFollowerSucceeded(boolean isFollower) {
        view.updateButtonView(isFollower);
    }

    @Override
    public void isFollowerFailed(String message) {
        view.showErrorMessage(message);
    }




    @Override
    public void unfollowSucceeded(boolean isFollowing) {
        view.updateButtonView(isFollowing);
        updateSelectedUserFollowingAndFollowers();
    }

    @Override
    public void unfollowFailed(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void followSucceeded(boolean isFollowing) {
        view.updateButtonView(isFollowing);
        updateSelectedUserFollowingAndFollowers();
    }

    @Override
    public void followFailed(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void updateSelectedUserFollowingAndFollowers() {
        view.updateSelectedUserFollowingAndFollowers();
    }

    @Override
    public void updateFollowButton(boolean isFollowing) {
        view.updateButtonView(isFollowing);
    }

}
