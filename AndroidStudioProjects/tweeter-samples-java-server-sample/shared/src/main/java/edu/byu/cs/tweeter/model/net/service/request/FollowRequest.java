package edu.byu.cs.tweeter.model.net.service.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {

    //private User follower;

    private String followerAlias;
   // private User followee;

    private String followeeAlias;
    private AuthToken authToken;


    public FollowRequest() {}

//    public FollowRequest(User followee, AuthToken authToken) { // i may need to remove this constructor
//        this.followee = followee;
//        this.authToken = authToken;
//    }

//    public FollowRequest(User follower, User followee, AuthToken authToken) {
//        this.follower = follower;
//        this.followee = followee;
//        this.authToken = authToken;
//    }

    public FollowRequest(String followerAlias, String followeeAlias, AuthToken authToken) {
        this.followerAlias = followerAlias;
        this.followeeAlias = followeeAlias;
        this.authToken = authToken;
    }

//    public User getFollowee() {
//        return followee;
//    }
//
//    public void setFollowee(User followee) {
//        this.followee = followee;
//    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

//    public User getFollower() {
//        return follower;
//    }
//
//    public void setFollower(User follower) {
//        this.follower = follower;
//    }


    public String getFollowerAlias() {
        return followerAlias;
    }

    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }




}
