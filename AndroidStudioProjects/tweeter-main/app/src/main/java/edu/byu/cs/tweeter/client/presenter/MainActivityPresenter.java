package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter {


    private View view;
    private User user;


    public interface View{

        void showFollowInfoMessage(String message);
        void showUnfollowInfoMessage(String message);
        void showErrorMessage(String message);


    }

    public MainActivityPresenter(View view, User user){
        this.view = view;
        this.user = user;
    }


    public void followTask(AuthToken authToken, User selectedUser){

        view.showFollowInfoMessage("Adding " + selectedUser.getName() + "...");

    }

    public void unfollowTask(AuthToken authToken, User selectedUser){

        view.showUnfollowInfoMessage("Removing " + selectedUser.getName() + "...");

    }





}
