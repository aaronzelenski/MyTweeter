package edu.byu.cs.tweeter.model.net.service.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryResponse extends PagedResponse {


    private List<Status> statuses;


    public GetStoryResponse(String message) {
        super(false, message, false);
    }

    public GetStoryResponse(List<Status> statuses, boolean hasMorePages) {
        super(true, hasMorePages);
        this.statuses = statuses;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetStoryResponse that = (GetStoryResponse) o;

        return Objects.equals(statuses, that.statuses);
    }

    @Override
    public int hashCode() {
        return statuses != null ? statuses.hashCode() : 0;
    }
}
