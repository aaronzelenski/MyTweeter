package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.service.response.LoginResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class LoginHandler implements RequestHandler<LoginRequest, LoginResponse> {
    @Override
    public LoginResponse handleRequest(LoginRequest loginRequest, Context context) {
        DynamoDBFactoryDAO factoryDAO = new DynamoDBFactoryDAO();
        UserService userService = new UserService(factoryDAO);
        try{
            return userService.login(loginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
