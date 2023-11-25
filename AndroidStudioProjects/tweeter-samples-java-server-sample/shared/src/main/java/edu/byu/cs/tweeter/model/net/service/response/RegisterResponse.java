package edu.byu.cs.tweeter.model.net.service.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterResponse extends Response {

    private User user;
    private AuthToken authToken;


    // create a response indicating that the corresponding request was unsuccessful.
    public RegisterResponse(String message) {
        super(false, message);
    }

    // create a response indicating that the corresponding request was successful.
    public RegisterResponse(User user, AuthToken authToken) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
    }

    // Returns the logged in user.
    public User getUser() {
        return user;
    }

    // Returns the auth token.
    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }


}
