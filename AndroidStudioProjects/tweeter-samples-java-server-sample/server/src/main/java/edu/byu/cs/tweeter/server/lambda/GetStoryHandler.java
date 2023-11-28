package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetStoryHandler implements RequestHandler<GetStoryRequest, GetStoryResponse> {
    @Override
    public GetStoryResponse handleRequest(GetStoryRequest request, Context context) {
        DynamoDBFactoryDAO factoryDAO = new DynamoDBFactoryDAO();
        StatusService service = new StatusService(factoryDAO);
        try {
            return service.getStory(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
