package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import edu.byu.cs.tweeter.server.SQSMessageSender;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.dao.FeedDTO;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeedsHandler implements RequestHandler<SQSEvent, Void> {
    private SQSMessageSender sqs = new SQSMessageSender("https://sqs.us-east-2.amazonaws.com/677926434568/UpdateFeedQueue");
    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        DynamoDBFactoryDAO dynamoDBFactoryDAO = new DynamoDBFactoryDAO();
        StatusService statusService = new StatusService(dynamoDBFactoryDAO);
        try {
            for (SQSEvent.SQSMessage msg : input.getRecords()) {
                FeedDTO feedDTO = sqs.getGson().fromJson(msg.getBody(), FeedDTO.class);
                statusService.batchUpdateFeeds(feedDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}