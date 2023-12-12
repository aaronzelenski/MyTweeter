package edu.byu.cs.tweeter.server.dao;


import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

// this class is used to store the followers with the appropriate tacked on status
public class FeedDTO {


    Status status;

    List<String> followersAliases;


    public FeedDTO(Status status, List<String> followersAliases) {
        this.status = status;
        this.followersAliases = followersAliases;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getFollowersAliases() {
        return followersAliases;
    }

    public void setFollowersAliases(List<String> followersAliases) {
        this.followersAliases = followersAliases;
    }
}
