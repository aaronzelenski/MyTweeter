package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetFeedHandler implements RequestHandler<GetFeedRequest, GetFeedResponse> {
    @Override
    public GetFeedResponse handleRequest(GetFeedRequest request, Context context) {
        DynamoDBFactoryDAO factoryDAO = new DynamoDBFactoryDAO();
        StatusService service = new StatusService(factoryDAO);
        try {
            return service.getFeed(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
