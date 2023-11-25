package edu.byu.cs.tweeter.model.net.service.response;

public class IsFollowerResponse extends Response{


    private boolean isFollower;

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean follower) {
        isFollower = follower;
    }
}
