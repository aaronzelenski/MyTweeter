package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
public class Feed implements Serializable {

    private String followerAlias; // alias of the followers of the user that we are going to tack on the status to
    private String userAlias; // alias of the user who posted the status
    private String post;
    private User user;
    private Long timestamp;
    private List<String> urls;
    private List<String> mentions;


    public Feed() {
    }
    public Feed(String followerAlias, String userAlias, String post, User user, Long timestamp, List<String> urls, List<String> mentions) {
        this.followerAlias = followerAlias;
        this.userAlias = userAlias;
        this.post = post;
        this.user = user;
        this.timestamp = timestamp;
        this.urls = urls;
        this.mentions = mentions;
    }


    @DynamoDbPartitionKey
    public String getFollowerAlias() {
        return followerAlias;
    }
    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }
}




