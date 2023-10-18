package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;
import android.os.Looper;
import android.os.Message;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserHandler extends HandlerGeneral {

    private UserService.GetUserObserver observer;

    public GetUserHandler(UserService.GetUserObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
        observer.getUserSucceeded(user);
    }

    @Override
    protected void handleFailure(String message) {
        observer.handleFailure(message);
    }

    @Override
    protected void handleException(Exception ex) {
        observer.handleFailure("Failed to get user's profile because of exception: " + ex.getMessage());
    }
}
