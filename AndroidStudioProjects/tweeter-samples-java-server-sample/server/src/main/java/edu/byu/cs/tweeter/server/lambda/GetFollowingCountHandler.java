package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<GetFollowingCountRequest, GetFollowingCountResponse> {
    @Override
    public GetFollowingCountResponse handleRequest(GetFollowingCountRequest request, Context context) {

        DynamoDBFactoryDAO dynamoDBFactoryDAO = new DynamoDBFactoryDAO();
        FollowService service = new FollowService(dynamoDBFactoryDAO);

        try{
            return service.getFollowingCount(request);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
