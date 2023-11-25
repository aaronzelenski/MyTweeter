package edu.byu.cs.tweeter.server.service;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import java.sql.Timestamp;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.service.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.service.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.service.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.service.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IFactoryDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.lambda.RegisterHandler;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {


    private static final Logger logger = Logger.getLogger(RegisterHandler.class.getName());

    private IUserDAO userDAO;
    private IAuthTokenDAO authDAO;
    private IFollowDAO followDAO;

    public UserService(IFactoryDAO factoryDAO) {
        userDAO = factoryDAO.getUserDAO();
        authDAO = factoryDAO.getAuthTokenDAO();
        followDAO = factoryDAO.getFollowDAO();
    }

    public UserService() {}

    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new RuntimeException("[400] Missing a username");
        } else if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("[400] Missing a password");
        }

        request.setPassword(hashPassword(request.getPassword()));

        LoginResponse response = userDAO.loginUser(request);
        if (response.isSuccess()) {
            String token = UUID.randomUUID().toString();
            long curr_time = new Timestamp(System.currentTimeMillis()).getTime();

            String storedToken = String.valueOf(authDAO.addToken(token, curr_time));
            AuthToken authToken = new AuthToken(storedToken, curr_time);

            response.setAuthToken(authToken);
            response.setUser(response.getUser());

        }
        return response;
    }


    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }

    public RegisterResponse register(RegisterRequest request) throws Exception {
        if (isInvalidRequest(request)) {
            throw new RuntimeException("[Bad Request] Missing a field");
        }

        String hashedPassword = hashPassword(request.getPassword());
        request.setPassword(hashedPassword);

        // check to see if there is an image
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            String s3ImageUrl = userDAO.uploadImageToS3(request.getImageUrl(), request.getUsername());
            request.setImageUrl(s3ImageUrl);
        }

        RegisterResponse response = userDAO.registerUser(request);


        if (response.isSuccess()) {
            String token = UUID.randomUUID().toString();
            long curr_time = new Timestamp(System.currentTimeMillis()).getTime();

            String storedToken = String.valueOf(authDAO.addToken(token, curr_time));
            AuthToken authToken = new AuthToken(storedToken, curr_time);

            response.setAuthToken(authToken);
        }
        return response;
    }



    public GetUserResponse getUser(GetUserRequest request) {
        try {
            return userDAO.getUser(request);
        } catch (Exception e) {
            throw new RuntimeException("[Bad Request] Missing something");
        }
    }


    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Missing a status");
        }
        Status status = getFakeData().getFakeStatuses().get(0);
        return new PostStatusResponse(status);
    }


    private boolean isInvalidRequest(RegisterRequest request) {
        return request.getFirstName().isEmpty() ||
                request.getLastName().isEmpty() ||
                request.getUsername().isEmpty() ||
                request.getPassword().isEmpty() ||
                request.getImageUrl().isEmpty();
    }


    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }




















    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    private User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    private AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    private FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
