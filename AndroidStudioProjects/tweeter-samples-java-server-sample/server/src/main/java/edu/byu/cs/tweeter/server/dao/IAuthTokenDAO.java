package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {

    String addToken(String token, long timestamp);
    public AuthToken validateToken(String token);
    //void deleteToken(String token);
    void deleteAllTokens();
}
