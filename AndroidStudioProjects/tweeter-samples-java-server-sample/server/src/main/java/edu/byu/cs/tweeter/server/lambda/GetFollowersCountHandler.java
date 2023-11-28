package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersCountHandler implements RequestHandler<GetFollowersCountRequest, GetFollowersCountResponse> {

    @Override
    public GetFollowersCountResponse handleRequest(GetFollowersCountRequest request, Context context) {

        DynamoDBFactoryDAO dynamoDBFactoryDAO = new DynamoDBFactoryDAO();
        FollowService service = new FollowService(dynamoDBFactoryDAO);

        try {
            return service.getFollowersCount(request);
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
}
