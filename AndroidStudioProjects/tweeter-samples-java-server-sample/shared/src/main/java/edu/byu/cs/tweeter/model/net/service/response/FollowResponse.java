package edu.byu.cs.tweeter.model.net.service.response;

import edu.byu.cs.tweeter.model.domain.User;

public class FollowResponse extends Response{
    public FollowResponse() {
        super(true, null);
    }
    public FollowResponse(String message) {
        super(false, message);
    }

}
