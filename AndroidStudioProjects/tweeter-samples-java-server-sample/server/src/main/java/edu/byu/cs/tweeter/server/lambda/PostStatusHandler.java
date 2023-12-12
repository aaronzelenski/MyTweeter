package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.logging.Logger;

import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.JsonSerializer;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.service.UserService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    private static final Logger logger = Logger.getLogger(PostStatusHandler.class.getName());


    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        DynamoDBFactoryDAO  dynamoDBFactoryDAO = new DynamoDBFactoryDAO();
        StatusService statusService = new StatusService(dynamoDBFactoryDAO);
        try{
            return statusService.postStatus(request);
        }catch (Exception e){
            return new PostStatusResponse(e.getMessage());
        }
    }
}
