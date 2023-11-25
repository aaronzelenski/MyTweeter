package edu.byu.cs.tweeter.model.net.service.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedResponse extends PagedResponse{

    private List<Status> statuses;


    public GetFeedResponse(String message){
        super(false, message, false);
    }

    public GetFeedResponse(List<Status> statuses, boolean hasMorePages){
        super(true, hasMorePages);
        this.statuses = statuses;
    }

    public List<Status> getStatuses(){
        return statuses;
    }

    @Override
    public boolean equals(Object param){
        if(this == param){
            return true;
        }

        if(param == null || getClass() != param.getClass()){
            return false;
        }

        GetFeedResponse that = (GetFeedResponse) param;

        return (Objects.equals(statuses, that.statuses)) &&
                this.isSuccess() == that.isSuccess();
    }


    @Override
    public int hashCode(){
        return Objects.hash(statuses);
    }



}
