package edu.byu.cs.tweeter.client.presenter;
import android.view.View;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T>{

    public User user;
    public View view;
    public boolean hasMorePages;
    public static final int PAGE_SIZE = 10;
    public boolean isLoading = false;


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public interface PagedView<U> {
        void setLoading(boolean value);
        void addItems(List<U> newItems);
        void showInfoMessage(String s);
        void showErrorMessage(String message);
        void openMainView(User user);
        void startingLoading();
        void endingLoading();
        void hideInfoMessage();

    }

    public boolean getIsLoading() {
        return isLoading;
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }
}
