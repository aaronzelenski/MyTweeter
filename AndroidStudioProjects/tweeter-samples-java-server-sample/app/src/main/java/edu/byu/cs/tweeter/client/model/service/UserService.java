package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public static final String LOGIN_URL_PATH = "/login";
    public static final String LOGOUT_URL_PATH = "/logout";
    public static final String REGISTER_URL_PATH = "/register";
    public static final String GET_USER_URL_PATH = "/getuser";

    public UserService() {}


    public interface LoginObserver {
        void loginSucceeded(User user, AuthToken authToken);
        void loginFailed(String message);

    }

    public void login(String username, String password, LoginObserver observer) {
        LoginTask loginTask = new LoginTask(this ,username,password,
                new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }

    public interface RegisterObserver {
        void registerSucceeded(User user, AuthToken authToken);
        void registerFailed(String message);
    }


    public void register(String firstName, String lastName, String alias, String password, String image,
                         RegisterObserver observer) {
        RegisterTask registerTask = new RegisterTask(this, firstName, lastName,
                alias, password, image, new RegisterHandler(observer));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }


    public interface GetUserObserver {
        void getUserSucceeded(User user);
        void getUserFailed(String message);
    }

    public void getUser(AuthToken authToken,String username, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(this, authToken, username, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);

    }


    public interface LogoutObserver {
        void logoutSucceeded();
        void logoutFailed(String message);
    }


    public void logout(AuthToken authToken, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(this, authToken, new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }




}
