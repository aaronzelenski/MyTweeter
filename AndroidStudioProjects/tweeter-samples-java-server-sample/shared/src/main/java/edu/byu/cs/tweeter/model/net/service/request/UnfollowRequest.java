package edu.byu.cs.tweeter.model.net.service.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest {

        private AuthToken authToken;

        private User unFollower; // The user that is going to unfollow
        private User unFollowee; // The user that is going to be unfollowed
        public UnfollowRequest() {}

        public UnfollowRequest(AuthToken authToken, User unFollower, User unFollowee) {
            this.authToken = authToken;
            this.unFollower = unFollower;
            this.unFollowee = unFollowee;
        }

        public AuthToken getAuthToken() {
                return authToken;
        }

        public void setAuthToken(AuthToken authToken) {
                this.authToken = authToken;
        }

        public User getUnFollower() {
                return unFollower;
        }
        public void setUnFollower(User unFollower) {
                this.unFollower = unFollower;
        }
        public User getUnFollowee() {
                return unFollowee;
        }
        public void setUnFollowee(User unFollowee) {
                this.unFollowee = unFollowee;
        }
}
