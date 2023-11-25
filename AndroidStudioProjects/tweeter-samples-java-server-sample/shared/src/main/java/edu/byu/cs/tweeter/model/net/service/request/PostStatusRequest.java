package edu.byu.cs.tweeter.model.net.service.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusRequest {

    public PostStatusRequest() {}

    private AuthToken authToken;
    private Status status;

    public PostStatusRequest(AuthToken authToken, Status status) {
        this.authToken = authToken;
        this.status = status;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public Status getStatus() {
        return status;
    }
}
