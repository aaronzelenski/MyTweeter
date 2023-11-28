package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;

public interface IStatusDAO {

    PostStatusResponse postStatus(PostStatusRequest request);

    GetStoryResponse getStory(GetStoryRequest request);

    GetFeedResponse getFeed(GetFeedRequest request);


}
