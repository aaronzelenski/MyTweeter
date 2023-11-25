package edu.byu.cs.tweeter.model.net.service.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusResponse extends Response{

    private Status status;


    public PostStatusResponse(String message) {
        super(false, message);
    }


    public PostStatusResponse(Status status) {
        super(true, null);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
