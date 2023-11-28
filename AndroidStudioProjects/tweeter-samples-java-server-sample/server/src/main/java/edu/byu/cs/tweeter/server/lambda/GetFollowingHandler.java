package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingHandler implements RequestHandler<FollowingRequest, FollowingResponse> {
    @Override
    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        DynamoDBFactoryDAO dynamoDBFactoryDAO = new DynamoDBFactoryDAO();
        FollowService service = new FollowService(dynamoDBFactoryDAO);
        try {
            return service.getFollowing(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
