package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.BatchPostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersResponse;
import edu.byu.cs.tweeter.server.SQSMessageSender;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.dao.FeedDTO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.JsonSerializer;



// this class is to first get the status
// then get the followers of that specific user that sent the status
// then use my DTO (contains my list of followers and status) to send to the queue #2
// then the queue #2 will send the DTO to the lambda function that will update the feed table


public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {
    private static final Logger logger = Logger.getLogger(PostUpdateFeedMessages.class.getName());
    private static final FollowDAO followDAO = new FollowDAO();
    private static final String queueURL = "https://sqs.us-east-2.amazonaws.com/677926434568/UpdateFeedQueue";
    private static final SQSMessageSender sqsMessageSender = new SQSMessageSender(queueURL);
    private static final AuthToken authToken = new AuthToken("fakeToken");

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for(SQSEvent.SQSMessage msg : event.getRecords()){
            String messageBody = msg.getBody();

            Status status = sqsMessageSender.getGson().fromJson(messageBody, Status.class);

            int batchSize = 25;
            List<String> allFollowerAliases = new ArrayList<>();
            String lastFollowerAlias = null;
            DataPage<Follow> followDataPage;

            do{

                if(lastFollowerAlias == null){
                    followDataPage = followDAO.getFollowers(new GetFollowersRequest(authToken, status.getUser().getAlias(), batchSize, null));
                } else {
                    followDataPage = followDAO.getFollowers(new GetFollowersRequest(authToken, status.getUser().getAlias(), batchSize, lastFollowerAlias));
                }

                List<String> followerAliases = getAliases(followDataPage);

                allFollowerAliases.addAll(followerAliases);


                if(!followerAliases.isEmpty()){
                    lastFollowerAlias = followerAliases.get(followerAliases.size() - 1);
                }

                if(allFollowerAliases.size() >= batchSize){
                    FeedDTO feedDTO = new FeedDTO(status, allFollowerAliases);
                    sqsMessageSender.sendMessage(feedDTO);
                    allFollowerAliases.clear();
                }
            }
            while(followDataPage != null && followDataPage.isHasMorePages());


            if(!allFollowerAliases.isEmpty()){
                FeedDTO feedDTO = new FeedDTO(status, allFollowerAliases);
                sqsMessageSender.sendMessage(feedDTO);
            }
        }
        return null;
    }


    private List<String> getAliases(DataPage<Follow> followDataPage) {
        List<String> aliases = new ArrayList<>();
        for (Follow follow : followDataPage.getValues()) {
            if(follow != null){
                aliases.add(follow.getFollower_handle());
            }
        }
        return aliases;
    }

}