package edu.byu.cs.tweeter.server.service;

import java.util.List;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
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
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFactoryDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.lambda.IsFollowerHandler;
import edu.byu.cs.tweeter.server.lambda.RegisterHandler;
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

            GetUserRequest getUserRequest = new GetUserRequest(request.getFollower(), request.getAuthToken());

            User follower = request.getFollower();
            AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());

            getUserRequest.setUser(follower);
            getUserRequest.setAuthToken(authToken);

            request.setFollower(userDAO.getUser(getUserRequest).getUser());
            return followDAO.follow(request);
        } catch (Exception e) {
            throw new Exception(request.getFollower() + " is empty or " + request.getFollowee() + "is empty");
        }
    }


    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        if (request.getUnFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }

        GetUserRequest getUserRequest = new GetUserRequest(request.getUnFollower(), request.getAuthToken());

        User unFollower = request.getUnFollower();
        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());

        getUserRequest.setUser(unFollower);
        getUserRequest.setAuthToken(authToken);

        request.setUnFollower(userDAO.getUser(getUserRequest).getUser());
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

        GetUserRequest getUserRequest = new GetUserRequest(request.getFollower(), request.getAuthToken());

        User follower = request.getFollower();
        AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());

        getUserRequest.setUser(follower);
        getUserRequest.setAuthToken(authToken);

        request.setFollower(userDAO.getUser(getUserRequest).getUser());


        logger.info(followDAO.isFollower(request).getIsFollower() + " is the response (true means they are a follower)");

        return followDAO.isFollower(request);
    }















    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        Pair<List<User>, Boolean> pair = getFollowingDAO().getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        return new FollowingResponse(pair.getFirst(), pair.getSecond());
    }



    public GetFollowersResponse getFollowers(GetFollowersRequest request){
        	if(request.getFollowerAlias() == null) {
                throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
            } else if(request.getLimit() <= 0) {
                throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
            }

        	Pair<List<User>, Boolean> pair = getFollowingDAO().getFollowers(request.getFollowerAlias(), request.getLimit(), request.getLastFollowerAlias());
            return new GetFollowersResponse(pair.getFirst(), pair.getSecond());
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request){
        if(request.getUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user");
        }else if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        int count = getFollowingDAO().getFollowerCount(request.getUser());
        return new GetFollowersCountResponse(count);
    }


    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request){
        if(request.getUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user");
        }else if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        int count = getFollowingDAO().getFolloweeCount(request.getUser());
        return new GetFollowingCountResponse(count);
    }











    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }
}
