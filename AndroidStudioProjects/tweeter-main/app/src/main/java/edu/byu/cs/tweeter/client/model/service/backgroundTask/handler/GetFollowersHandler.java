package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;
import android.os.Looper;
import android.os.Message;
import java.util.List;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersHandler extends HandlerGeneral {

    private final FollowService.GetFollowersObserver observer;

    public GetFollowersHandler(FollowService.GetFollowersObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    protected void handleSuccess(Message msg) {
        List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        //User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
        observer.getFollowersSuccess(followers, hasMorePages);
    }

    @Override
    protected void handleFailure(String message) {
        observer.getFollowersFailed(message);
    }
}