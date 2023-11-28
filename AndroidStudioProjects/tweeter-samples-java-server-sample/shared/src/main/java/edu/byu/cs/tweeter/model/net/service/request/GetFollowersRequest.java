package edu.byu.cs.tweeter.model.net.service.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowersRequest {

    public GetFollowersRequest() {}

    private AuthToken authToken;
    private String followeeAlias;
    private int limit;
    private String lastFollowerAlias;


    /**
     * Creates an instance.
     *
     * @param followeeAlias the alias of the user whose followees are to be returned.
     * @param limit the maximum number of followees to return.
     * @param lastFollowerAlias the alias of the last followee that was returned in the previous request (null if
     *                     there was no previous request or if no followees were returned in the
     *                     previous request).
     */
    public GetFollowersRequest(AuthToken authToken, String followeeAlias, int limit, String lastFollowerAlias) {
        this.authToken = authToken;
        this.followeeAlias = followeeAlias;
        this.limit = limit;
        this.lastFollowerAlias = lastFollowerAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastFollowerAlias() {
        return lastFollowerAlias;
    }

    public void setLastFollowerAlias(String lastFollowerAlias) {
        this.lastFollowerAlias = lastFollowerAlias;
    }
}
