package edu.byu.cs.tweeter.client.presenter;
import android.os.Message;
import java.util.ArrayList;
import java.util.List;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter implements StatusService.GetMainActivityObserver, StatusService.PostStatusObserver,
StatusService.LogoutObserver, StatusService.followerCountObserver, StatusService.followingCountObserver, StatusService.isFollowerObserver{

    private final View view;
    private final User user;

    public interface View{
        void showFollowInfoMessage(String message);
        void showUnfollowInfoMessage(String message);
        void showErrorMessage(String message);
        void updateFollowingAndFollowersCountView();
        void updateFollowButtonView(boolean isFollowing);
        void postShowInfoMessage(String message);
        void postShowErrorMessage(String message);
        void postShowSuccessMessage(String message);
        void showLogoutInfoMessage(String message);
        void showLogoutErrorMessage(String message);
        void postLogoutSuccessMessage(String message);
        void logoutUser();
        void updateFollowerSuccess(Message msg);
        void updateFollowerFailed(String message);
        void updateFollowingSuccess(Message msg);
        void updateFollowingFailed(String message);
        void isFollowerFailed(String message);
        void isFollowerSucceeded(Message msg);
    }

    public MainActivityPresenter(View view, User user){
        this.view = view;
        this.user = user;
    }

    public void followTask(AuthToken authToken, User selectedUser){
        var statusService = new StatusService();
        statusService.followTask(authToken, selectedUser, this);
        view.showFollowInfoMessage("Adding " + selectedUser.getName() + "...");
    }

    public void unfollowTask(AuthToken authToken, User selectedUser){
        var statusService = new StatusService();
        statusService.unfollowTask(authToken, selectedUser, this);
        view.showUnfollowInfoMessage("Removing " + selectedUser.getName() + "...");
    }

    public void logout(AuthToken authToken){
        var statusService = new StatusService();
        statusService.logoutTask(authToken, this);
        view.showLogoutInfoMessage("Logging Out...");
    }



    public void getFollowersCount(AuthToken authToken, User selectedUser){
        var statusService = new StatusService();
        statusService.getFollowersCountTask(authToken, selectedUser, this);
    }


    public void getFollowingCount(AuthToken authToken, User selectedUser){
        var statusService = new StatusService();
        statusService.getFollowingCountTask(authToken, selectedUser, this);
    }


    public void isFollower(AuthToken authToken, User currUser, User selectedUser){
        var statusService = new StatusService();
        statusService.isFollowerTask(authToken, currUser,selectedUser,this);
    }

    public void postStatus(String post, User user, long currentTimeMillis, List<String> parseURLs, List<String> parseMentions){
        var statusService = new StatusService();
        statusService.postTask(post, user, currentTimeMillis, parseURLs(post), parseMentions(post), this);
        try{
            view.postShowInfoMessage("Posting Status...");
        }
        catch (Exception ex)
        {
            view.postShowErrorMessage("Failed to post the status because of exception: " + ex.getMessage());
    }
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



    @Override
    public void followTaskSucceeded(boolean isFollowing) {
        view.updateFollowButtonView(isFollowing);
        view.updateFollowingAndFollowersCountView();
    }
    @Override
    public void unfollowTaskSucceeded(boolean isFollowing) {
        view.updateFollowButtonView(isFollowing);
        view.updateFollowingAndFollowersCountView();
    }

    @Override
    public void isFollowerSucceeded(Message msg) {
        view.isFollowerSucceeded(msg);
    }
    @Override
    public void isFollowerFailed(String message) {
        view.isFollowerFailed(message);
    }
    @Override
    public void postStatusSucceeded(String message) {
        view.postShowSuccessMessage(message);
    }
    @Override
    public void postStatusFailed(String message) {
        view.postShowErrorMessage(message);
    }
    @Override
    public void followTaskFailed(String message) {
        view.showErrorMessage(message);
    }
    @Override
    public void unfollowTaskFailed(String message) {
        view.showErrorMessage(message);
    }
    @Override
    public void logoutSucceeded() {
        view.logoutUser();
    }
    @Override
    public void logoutFailed(String message) {
        view.showLogoutErrorMessage(message);
    }
    @Override
    public void updateFollowingCountSucceeded(Message msg) {
        view.updateFollowingSuccess(msg);
    }
    @Override
    public void updateFollowingCountFailed(String message) {
        view.updateFollowingFailed(message);
    }
    @Override
    public void updateFollowerCountSucceeded(Message msg) {
        view.updateFollowerSuccess(msg);
    }
    @Override
    public void updateFollowerCountFailed(String message) {
        view.updateFollowerFailed(message);
    }


}
