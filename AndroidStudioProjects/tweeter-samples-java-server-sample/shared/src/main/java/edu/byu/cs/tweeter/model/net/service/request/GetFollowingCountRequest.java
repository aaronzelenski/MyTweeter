package edu.byu.cs.tweeter.model.net.service.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingCountRequest {

    //private User user;

    String alias;
    private AuthToken authToken;

    public GetFollowingCountRequest() {}

    public GetFollowingCountRequest(String alias, AuthToken authToken) {
        this.alias = alias;
        this.authToken = authToken;
    }


//    public GetFollowingCountRequest(User user, AuthToken authToken) {
//        this.user = user;
//        this.authToken = authToken;
//    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
