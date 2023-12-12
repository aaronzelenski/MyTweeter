package edu.byu.cs.tweeter.server;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.server.service.JsonSerializer;

public class SQSMessageSender {

    private AmazonSQS sqs;
    private String queueUrl;
    private Gson gson;

    public SQSMessageSender(String queueUrl) {
        this.sqs = AmazonSQSClientBuilder.defaultClient();
        this.queueUrl = queueUrl;
        this.gson = new Gson();
    }

    public void sendMessage(Object message) {
        String messageBody = gson.toJson(message);

        try {
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody);

            sqs.sendMessage(send_msg_request);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Gson getGson() {
        return gson;
    }


}
