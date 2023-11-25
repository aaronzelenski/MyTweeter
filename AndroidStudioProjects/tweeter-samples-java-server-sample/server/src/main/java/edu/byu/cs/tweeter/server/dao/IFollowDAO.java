package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.service.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.service.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.service.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.service.response.UnfollowResponse;

public interface IFollowDAO {

        FollowResponse follow(FollowRequest request);

        UnfollowResponse unfollow(UnfollowRequest request);

        //FollowingResponse getFollowing(FollowingRequest request);

        //FollowersResponse getFollowers(FollowersRequest request);

        IsFollowerResponse isFollower(IsFollowerRequest request);

        //void deleteAllFollows();
}
