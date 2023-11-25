package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.service.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowHandler implements RequestHandler<UnfollowRequest, UnfollowResponse> {
    @Override
    public UnfollowResponse handleRequest(UnfollowRequest input, Context context) {
        DynamoDBFactoryDAO dynamoDBFactoryDAO = new DynamoDBFactoryDAO();
        FollowService followService = new FollowService(dynamoDBFactoryDAO);
        try {
            return followService.unfollow(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
