package edu.byu.cs.tweeter.model.net.service.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class BatchPostStatusRequest {

    List<String> followersAliases;

    Status status;

    public BatchPostStatusRequest(List<String> followersAliases, Status status) {
        this.followersAliases = followersAliases;
        this.status = status;
    }

    public List<String> getFollowersAliases() {
        return followersAliases;
    }

    public void setFollowersAliases(List<String> followersAliases) {
        this.followersAliases = followersAliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
