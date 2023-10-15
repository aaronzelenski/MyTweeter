package edu.byu.cs.tweeter.client.model.service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic to support the login operation.
 */
public class UserService {

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface LoginObserver {
        void handleSuccess(User user, AuthToken authToken);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    /**
     * Creates an instance.
     */
    public UserService() {
    }

    /**
     * Makes an asynchronous login request.
     *
     * @param username the user's name.
     * @param password the user's password.
     */
    public void login(String username, String password, LoginObserver observer) {
        LoginTask loginTask = getLoginTask(username, password, observer);
        BackgroundTaskUtils.runTask(loginTask);
    }

    /**
     * Returns an instance of {@link LoginTask}. Allows mocking of the LoginTask class for
     * testing purposes. All usages of LoginTask should get their instance from this method to
     * allow for proper mocking.
     *
     * @return the instance.
     */
    LoginTask getLoginTask(String username, String password, LoginObserver observer) {
        return new LoginTask(username, password, new LoginTaskHandler(observer));
    }

    // ------------------- GetUser ------------------- //

    public interface GetUserObserver {
        void getUserSucceeded(User user);
        void getUserFailed(String message);
    }

    public void getUser(AuthToken authToken,String clickable, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, clickable, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);

    }



    public interface RegisterObserver {
        void registerSucceeded(User user, AuthToken authToken);
        void registerSucceeded(User user);
        void registerFailed(String message);
        void handleException(Exception exception);

    }

    public void register(String firstName, String lastName, String alias, String password, String image,
                         RegisterObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName.toString(), lastName.toString(),
                alias.toString(), password.toString(), image, new RegisterHandler(observer));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }
}
