package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.service.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {



    public GetFeedResponse getFeed(GetFeedRequest request) {
        if(request.getUser() == null) {
            throw new RuntimeException("[Bad Request] no user given");
        }
        else if (request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] limit is less than 0");
        }
        Pair<List<Status>, Boolean> pair = getStatusDAO().getPageOfStatus(request.getLastStatus(),request.getLimit());
        return new GetFeedResponse(pair.getFirst(), pair.getSecond());
    }



    public GetStoryResponse getStory(GetStoryRequest request){
        if(request.getUser() == null) {
            throw new RuntimeException("[Bad Request] no user given");
        }
        else if (request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] limit is less than 0");
        }
        Pair<List<Status>, Boolean> pair = getStatusDAO().getPageOfStatus(request.getLastStatus(),request.getLimit());
        return new GetStoryResponse(pair.getFirst(), pair.getSecond());
    }










    public StatusDAO getStatusDAO() {
        return new StatusDAO();
    }




}
