package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ClientCommunicator;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.service.response.RegisterResponse;

public class GetFollowersCountTest {

    private final String URL_PATH = "/getfollowerscount";
    private AuthToken authToken;
    private User user;
    private ServerFacade serverFacade;
    private ClientCommunicator clientCommunicatorMock;


    @BeforeEach
    public void setUp() {
        clientCommunicatorMock = mock(ClientCommunicator.class);
        serverFacade = new ServerFacade();
        authToken = new AuthToken();
        user = new User("Test", "User", "@TestUser", "image");
    }


    @Test
    public void getFollowersCountSuccessful() throws IOException, TweeterRemoteException {

        GetFollowersCountRequest request = new GetFollowersCountRequest(user, authToken);
        GetFollowersCountResponse mockResponse = new GetFollowersCountResponse(21);

        when(clientCommunicatorMock.doPost(URL_PATH, request, null, GetFollowersCountResponse.class))
                .thenReturn(mockResponse);


        GetFollowersCountResponse response = serverFacade.getFollowersCount(request, URL_PATH);


        assertTrue(response.isSuccess());
        assertEquals(21, response.getFollowersCount());

    }

    @Test
    public void getFollowersCountUnsuccessful() throws IOException, TweeterRemoteException {

        GetFollowersCountRequest request = new GetFollowersCountRequest(user, authToken);
        GetFollowersCountResponse mockResponse = new GetFollowersCountResponse(23);

        when(clientCommunicatorMock.doPost(URL_PATH, request, null, GetFollowersCountResponse.class))
                .thenReturn(mockResponse);

        GetFollowersCountResponse response = serverFacade.getFollowersCount(request, URL_PATH);

        assertTrue(response.isSuccess());
        assertNotEquals(23, response.getFollowersCount());
    }



}
