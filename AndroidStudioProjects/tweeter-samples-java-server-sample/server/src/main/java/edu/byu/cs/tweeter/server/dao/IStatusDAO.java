package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Feed;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.service.request.BatchPostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;

public interface IStatusDAO {

    PostStatusResponse postStatus(PostStatusRequest request);

    GetStoryResponse getStory(GetStoryRequest request);

    DataPage<Feed> getFeed(GetFeedRequest request);

    void batchUpdateFeeds(List<Feed> feeds);


}
