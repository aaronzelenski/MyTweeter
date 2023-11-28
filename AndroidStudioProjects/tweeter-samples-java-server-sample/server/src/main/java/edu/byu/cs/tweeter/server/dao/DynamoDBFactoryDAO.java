package edu.byu.cs.tweeter.server.dao;

public class DynamoDBFactoryDAO implements IFactoryDAO{

    @Override
    public IUserDAO getUserDAO() {
        return new UserDAO();
    }
    @Override
    public IAuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAO();
    }
    @Override
    public IFollowDAO getFollowDAO() {
        return new FollowDAO();
    }
    @Override
    public IStatusDAO getStatusDAO() {return new StatusDAO();}
}
