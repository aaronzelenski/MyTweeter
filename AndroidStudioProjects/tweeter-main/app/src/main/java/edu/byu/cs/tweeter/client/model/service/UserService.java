package edu.byu.cs.tweeter.client.model.service;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends SuperService {

    public UserService() {}


    public interface LoginObserver {
        void handleSuccess(User user, AuthToken authToken);
        void handleFailure(String message);
        void handleException(Exception exception);
    }



    public void login(String username, String password, LoginObserver observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        getBackgroundTaskUtils(loginTask);
    }

    LoginTask getLoginTask(String username, String password, LoginObserver observer) {
        return new LoginTask(username, password, new LoginTaskHandler(observer));
    }

    // ------------------- GetUser ------------------- //

    public interface GetUserObserver {
        void getUserSucceeded(User user);
        void handleFailure(String message);
    }

    public void getUser(AuthToken authToken,String clickable, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, clickable, new GetUserHandler(observer));
        executeTask(getUserTask);

    }

    public interface RegisterObserver {
        void registerSucceeded(User user, AuthToken authToken);
        void registerSucceeded(User user);
        void registerFailed(String message);
        void handleException(Exception exception);

    }

    public void register(String firstName, String lastName, String alias, String password, String image,
                         RegisterObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, image, new RegisterHandler(observer));
        executeTask(registerTask);
    }
}
