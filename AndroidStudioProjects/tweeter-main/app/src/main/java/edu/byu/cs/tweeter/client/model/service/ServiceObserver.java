package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

public interface ServiceObserver<T> {
    void onSuccess(List<T> items, boolean hasMorePages);
    void onFailure(String message);
    void onException(Exception exception);
}
