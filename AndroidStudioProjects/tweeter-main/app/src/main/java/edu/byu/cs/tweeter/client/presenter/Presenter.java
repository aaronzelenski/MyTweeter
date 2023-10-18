package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class Presenter {

    private final View view;
    private final User user;

    public Presenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public interface View{
    }




}
