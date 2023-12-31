package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.service.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.service.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.service.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.service.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFactoryDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.lambda.IsFollowerHandler;
import edu.byu.cs.tweeter.util.Pair;


public class FollowService {


    private IUserDAO userDAO;
    private IAuthTokenDAO authDAO;
    private IFollowDAO followDAO;

    private static final Logger logger = Logger.getLogger(IsFollowerHandler.class.getName());

    public FollowService(IFactoryDAO factoryDAO) {
        userDAO = factoryDAO.getUserDAO();
        authDAO = factoryDAO.getAuthTokenDAO();
        followDAO = factoryDAO.getFollowDAO();
    }

    public FollowService() {}


    public FollowResponse follow(FollowRequest request) throws Exception {
        try {

            AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
            request.setAuthToken(authToken);

            GetUserRequest getUserRequest = new GetUserRequest(request.getFollowerAlias(), request.getAuthToken());
            userDAO.getUser(getUserRequest);

            return followDAO.follow(request);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        if (request.getUnFollowerAlias() == null || request.getUnFollowerAlias().isEmpty()) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        if (request.getUnFolloweeAlias() == null || request.getUnFolloweeAlias().isEmpty()) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }

        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
        request.setAuthToken(authToken);


        GetUserRequest getUserRequest = new GetUserRequest(request.getUnFollowerAlias(), request.getAuthToken());
        userDAO.getUser(getUserRequest);
//
//        User unFollower = request.getUnFollower();
//
//
//        request.setUnFollower(userDAO.getUser(getUserRequest).getUser());
        return followDAO.unfollow(request);
    }


    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a AuthToken");
        }
        if(request.getFollower() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }

        GetUserRequest getUserRequest = new GetUserRequest(request.getFollower().getAlias(), request.getAuthToken());

        User follower = request.getFollower();
        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());

        getUserRequest.setUser(follower);
        getUserRequest.setAuthToken(authToken);

        request.setFollower(userDAO.getUser(getUserRequest).getUser());


        logger.info(followDAO.isFollower(request).getIsFollower() + " is the response (true means they are a follower)");

        return followDAO.isFollower(request);
    }


    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request){
        if(request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }else if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }

        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
        request.setAuthToken(authToken);
        GetUserRequest getUserRequest = new GetUserRequest(request.getAlias(), authToken);
        userDAO.getUser(getUserRequest);

        return followDAO.getFollowersCount(request);
    }


    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        // Validate the request
        if (request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }

        // Validate the AuthToken and ensure the user exists
        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
        request.setAuthToken(authToken);
        GetUserRequest getUserRequest = new GetUserRequest(request.getAlias(), authToken);
        userDAO.getUser(getUserRequest);

        // Call the DAO method to get following count
        return followDAO.getFollowingCount(request);
    }

    public FollowingResponse getFollowing(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
        request.setAuthToken(authToken);

        return followDAO.getFollowing(request);
    }


    public GetFollowersResponse getFollowers(GetFollowersRequest request){
        	if(request.getFolloweeAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }

        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
        request.setAuthToken(authToken);

        DataPage<Follow> result = followDAO.getFollowers(request);
        List<String> aliases = getAliases(result);
        List<User> users = userDAO.getUserList(aliases);

        return new GetFollowersResponse(users, result.isHasMorePages());


    }


    private List<String> getAliases(DataPage<Follow> followDataPage) {
        List<String> aliases = new ArrayList<>();
        for (Follow follow : followDataPage.getValues()) {
            if(follow != null){
                aliases.add(follow.getFollower_handle());
            }
        }
        return aliases;
    }

}
