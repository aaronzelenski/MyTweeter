package edu.byu.cs.tweeter.server.dao;

public interface IFactoryDAO {

    IUserDAO getUserDAO();
    IAuthTokenDAO getAuthTokenDAO();

    IFollowDAO getFollowDAO();

}
