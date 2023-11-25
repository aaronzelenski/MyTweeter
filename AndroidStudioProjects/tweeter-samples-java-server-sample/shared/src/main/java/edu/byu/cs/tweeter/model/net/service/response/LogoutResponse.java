package edu.byu.cs.tweeter.model.net.service.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutResponse extends Response{

    private AuthToken authToken;

    public LogoutResponse(String message) {
        super(false, message);
    }

    public LogoutResponse(boolean success, AuthToken authToken) {
        super(true, null);
        this.authToken = authToken;
    }

    public LogoutResponse(boolean success) {
        super(true, null);
    }


    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
