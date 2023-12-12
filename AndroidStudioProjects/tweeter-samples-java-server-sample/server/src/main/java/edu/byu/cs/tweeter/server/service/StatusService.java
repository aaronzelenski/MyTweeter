package edu.byu.cs.tweeter.server.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Feed;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.BatchPostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.SQSMessageSender;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.FeedDTO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFactoryDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.lambda.PostStatusHandler;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {

    private IStatusDAO statusDAO;
    private IAuthTokenDAO authDAO;
    private IFollowDAO followDAO;
    private IUserDAO userDAO;

    private static final Logger logger = Logger.getLogger(PostStatusHandler.class.getName());
    String queueURL = "https://sqs.us-east-2.amazonaws.com/677926434568/PostStatusQueue";

    SQSMessageSender sqsMessageSender = new SQSMessageSender(queueURL);


    public StatusService(){};

    public StatusService(IFactoryDAO factoryDAO) {
        this.statusDAO = factoryDAO.getStatusDAO();
        this.authDAO = factoryDAO.getAuthTokenDAO();
        this.followDAO = factoryDAO.getFollowDAO();
        this.userDAO = factoryDAO.getUserDAO();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null || request.getStatus().getPost().isEmpty()) {
            throw new IllegalArgumentException("[Bad Request] Missing status content");
        }
        if (request.getAuthToken() == null) {
            throw new IllegalArgumentException("[Bad Request] Missing authentication token");
        }

        try {
            AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());
            request.setAuthToken(authToken);

            sqsMessageSender.sendMessage(request.getStatus()); // send message to the queue (might need to send the whole request)

            return statusDAO.postStatus(request);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error posting status: " + request.getStatus().getPost() + " by " + request.getStatus().getUser().getAlias(), e);
            throw new RuntimeException("[Bad Request] " + e.getMessage());
        }
    }

    private List<String> getAliases(List<User> users) {
        List<String> aliases = new ArrayList<>();
        for (User user : users) {
            aliases.add(user.getAlias());
        }
        return aliases;
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
            throw new IllegalArgumentException("[Bad Request] User information is required");}
        if (request.getLimit() <= 0) {
            throw new IllegalArgumentException("[Bad Request] Limit must be positive");}
        if (request.getAuthToken() == null) {
            throw new IllegalArgumentException("[Bad Request] Authentication token is required");}

        try {
            AuthToken authToken = authDAO.validateToken(request.getAuthToken().toString());

            DataPage<Feed> result = statusDAO.getFeed(request);

            List<Status> statuses = getStatuses(result, authToken);

            return new GetFeedResponse(statuses, result.isHasMorePages());
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving story for user: " + request.getUser().getAlias(), e);
            throw new RuntimeException("[Bad Request] " + e.getMessage());
        }
    }



    private List<Status> getStatuses(DataPage<Feed> result, AuthToken authToken){

        List<Status> statuses = new ArrayList<>();

        for (Feed feed : result.getValues()) {
            GetUserRequest getUserRequest = new GetUserRequest(feed.getUser().getAlias(), authToken);
            GetUserResponse getUserResponse = userDAO.getUser(getUserRequest);
            User user = getUserResponse.getUser();
            Status status = new Status(feed.getPost(), user, feed.getTimestamp(), feed.getMentions(), feed.getUrls());
            statuses.add(status);
        }


        return statuses;
    }



    public void batchUpdateFeeds(FeedDTO feedDTO) {
        // going to have to get the followers of the user that sent the status

        List<String> followerAliases = feedDTO.getFollowersAliases();
        Status status = feedDTO.getStatus();

        List<Feed> feeds = new ArrayList<>();
        for (String followerAlias : followerAliases) {
            Feed feed = new Feed();
            feed.setUserAlias(status.getUser().getAlias());
            feed.setTimestamp(System.currentTimeMillis());
            feed.setPost(status.getPost());
            feed.setUser(status.getUser());
            feed.setUrls(status.getUrls());
            feed.setMentions(status.getMentions());
            feed.setFollowerAlias(followerAlias);
            feeds.add(feed);
        }


        statusDAO.batchUpdateFeeds(feeds);
    }



//    public void deleteAllFeeds() {
//        statusDAO.deleteAllFeeds();
//    }








//    private List<String> getFollowingUsers(String userAlias) {
//        List<String> followingUsers = new ArrayList<>();
//        String lastFolloweeAlias = null;
//        boolean hasMorePages;
//
//        do{
//            FollowingRequest request = new FollowingRequest(new AuthToken(), userAlias, 10, lastFolloweeAlias);
//            FollowingResponse response = followDAO.getFollowing(request);
//            List<User> follows = response.getFollowees();
//            for (User user : follows) {
//                followingUsers.add(user.getAlias());
//            }
//
//            hasMorePages = response.getHasMorePages();
//            if (hasMorePages) {
//                lastFolloweeAlias = follows.get(follows.size() - 1).getAlias();
//            }
//
//        } while (hasMorePages);
//        return followingUsers;
//    }





}
