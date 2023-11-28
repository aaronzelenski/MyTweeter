package edu.byu.cs.tweeter.server.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFactoryDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.lambda.PostStatusHandler;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {

    private IStatusDAO statusDAO;

    private IAuthTokenDAO authDAO;

    private static final Logger logger = Logger.getLogger(PostStatusHandler.class.getName());


    public StatusService(){};

    public StatusService(IFactoryDAO factoryDAO) {
        this.statusDAO = factoryDAO.getStatusDAO();
        this.authDAO = factoryDAO.getAuthTokenDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().getPost().isEmpty()) {
            throw new IllegalArgumentException("[Bad Request] Missing status content");
        }
        if (request.getAuthToken() == null) {
            throw new IllegalArgumentException("[Bad Request] Missing authentication token");
        }

        logger.info("Posting status: " + request.getStatus().getPost() + " by " + request.getStatus().getUser().getAlias());

        return statusDAO.postStatus(request);
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        if (request.getUser() == null || request.getUser().getAlias() == null) {
            throw new IllegalArgumentException("[Bad Request] User information is required");
        }
        if (request.getLimit() <= 0) {
            throw new IllegalArgumentException("[Bad Request] Limit must be positive");
        }
        if (request.getAuthToken() == null) {
            throw new IllegalArgumentException("[Bad Request] Authentication token is required");
        }

        try {
            AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
            String userAlias = request.getUser().getAlias();
            int limit = request.getLimit();
            Status lastStatus = request.getLastStatus();

            logger.info("Retrieving STORY for user: " + userAlias);

            GetStoryRequest validatedRequest = new GetStoryRequest(authToken, request.getUser(), limit, lastStatus);

            return statusDAO.getStory(validatedRequest);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving story for user: " + request.getUser().getAlias(), e);
            throw new RuntimeException("[Bad Request] " + e.getMessage());
        }
    }


    public GetFeedResponse getFeed(GetFeedRequest request) {
        if (request.getUser() == null || request.getUser().getAlias() == null) {
            throw new IllegalArgumentException("[Bad Request] User information is required");
        }
        if (request.getLimit() <= 0) {
            throw new IllegalArgumentException("[Bad Request] Limit must be positive");
        }
        if (request.getAuthToken() == null) {
            throw new IllegalArgumentException("[Bad Request] Authentication token is required");
        }


        try {
            AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
            String userAlias = request.getUser().getAlias();
            int limit = request.getLimit();
            Status lastStatus = request.getLastStatus();

            logger.info("Retrieving FEED for user: " + userAlias);

            GetFeedRequest validatedRequest = new GetFeedRequest(authToken, request.getUser(), limit, lastStatus);

            return statusDAO.getFeed(validatedRequest);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving story for user: " + request.getUser().getAlias(), e);
            throw new RuntimeException("[Bad Request] " + e.getMessage());
        }
    }
}
