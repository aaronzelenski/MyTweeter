package edu.byu.cs.tweeter.model.net.service.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest {

        private AuthToken authToken;

//        private User unFollower;
//        private User unFollowee;\

        private String unFollowerAlias;

        private String unFolloweeAlias;

        public UnfollowRequest() {
        }

//        public UnfollowRequest(AuthToken authToken, User unFollower, User unFollowee) {
//            this.authToken = authToken;
//            this.unFollower = unFollower;
//            this.unFollowee = unFollowee;
//        }

        public AuthToken getAuthToken() {
                return authToken;
        }

        public void setAuthToken(AuthToken authToken) {
                this.authToken = authToken;
        }

//        public User getUnFollower() {
//                return unFollower;
//        }
//        public void setUnFollower(User unFollower) {
//                this.unFollower = unFollower;
//        }
//        public User getUnFollowee() {
//                return unFollowee;
//        }
//        public void setUnFollowee(User unFollowee) {
//                this.unFollowee = unFollowee;
//        }
//}

        public UnfollowRequest(AuthToken authToken, String unFollowerAlias, String unFolloweeAlias) {
                this.authToken = authToken;
                this.unFollowerAlias = unFollowerAlias;
                this.unFolloweeAlias = unFolloweeAlias;
        }

        public String getUnFollowerAlias() {
                return unFollowerAlias;
        }

        public void setUnFollowerAlias(String unFollowerAlias) {
                this.unFollowerAlias = unFollowerAlias;
        }

        public String getUnFolloweeAlias() {
                return unFolloweeAlias;
        }

        public void setUnFolloweeAlias(String unFolloweeAlias) {
                this.unFolloweeAlias = unFolloweeAlias;
        }


}