package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.CurrentStateService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
//import edu.byu.cs.tweeter.client.model.service.loginServiceProxy;
import edu.byu.cs.tweeter.client.view.login.LoginFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.service.response.LoginResponse;

public class LoginPresenter implements UserService.LoginObserver{

    private static final String LOG_TAG = "LoginPresenter";
    private final View view;

    private CurrentStateService currentStateService;

    public LoginPresenter(View view) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(view == null) {
            throw new NullPointerException();
        }
        this.view = view;
    }


    public interface View {
        // Professor code
        void showInfoMessage(String message);
        void hideInfoMessage();
        void showErrorMessage(String message);
        void hideErrorMessage();
        void openMainView(User user);
    }




    public void login(String username, String password) {
        if(validateLogin(username, password)) {
            view.hideErrorMessage();
            view.showInfoMessage("Logging in...");

            UserService userService = new UserService();
            userService.login(username, password, this);
        }
    }



    public boolean validateLogin(String username, String password) {
        if (username.length() > 0 && username.charAt(0) != '@') {
            view.showErrorMessage("Alias must begin with @.");
            return false;
        }
        if (username.length() < 2) {
            view.showErrorMessage("Alias must contain 1 or more characters after the @.");
            return false;
        }
        if (password.length() == 0) {
            view.showErrorMessage("Password cannot be empty.");
            return false;
        }
        return true;
    }


    // All of the following methods are from the UserService.LoginObserver interface
    @Override
    public void loginSucceeded(User user, AuthToken authToken) {
        view.hideInfoMessage();
        view.hideErrorMessage();
        view.showInfoMessage("Welcome back " + user.getName() + "!");
        view.openMainView(user);
    }

    @Override
    public void loginFailed(String message) {
        view.showErrorMessage(message);
    }

}
