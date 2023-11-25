package edu.byu.cs.tweeter.model.net.service.response;

import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowResponse extends Response{

    public UnfollowResponse() {
        super(true, null);
    }
    public UnfollowResponse(String message) {
        super(false, message);
    }
}
