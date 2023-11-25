package edu.byu.cs.tweeter.model.net.service.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersResponse extends PagedResponse{


    private List<User> followers;


    GetFollowersResponse(String message) {
        super(false, message, false);
    }

    public GetFollowersResponse(List<User> followers, boolean hasMorePages) {
        super(true, hasMorePages);
        this.followers = followers;
    }


    public List<User> getFollowers() {
        return followers;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetFollowersResponse that = (GetFollowersResponse) o;

        return Objects.equals(followers, that.followers);
    }

    @Override
    public int hashCode() {
        return followers != null ? followers.hashCode() : 0;
    }
}
