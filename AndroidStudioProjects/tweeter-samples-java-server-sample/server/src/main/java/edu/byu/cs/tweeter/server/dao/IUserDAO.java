package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.service.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.service.response.RegisterResponse;

public interface IUserDAO {

    RegisterResponse registerUser(RegisterRequest request);

    LoginResponse loginUser(LoginRequest request);

    String uploadImageToS3(String imageString, String alias);
    GetUserResponse getUser(GetUserRequest request);

    void deleteAllUsers();


}
