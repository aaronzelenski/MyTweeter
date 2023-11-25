package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ClientCommunicator;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.service.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.service.response.GetFollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class GetFollowersTest {

    private ServerFacade serverFacade;
    private ClientCommunicator clientCommunicatorMock;
    private final String URL_PATH = "/getfollower";

    AuthToken authToken;
    String followerAlias;
    String lastFollowerAlias;

    int limit = 10;



    @BeforeEach
    public void setUp() {
        clientCommunicatorMock = mock(ClientCommunicator.class);
        serverFacade = new ServerFacade();

        authToken = new AuthToken();
        followerAlias = "@user";
        lastFollowerAlias = null;

    }




    @Test
    public void testGetFollowers_returnsCorrectFollowers() throws IOException, TweeterRemoteException {
        FakeData fakeData = FakeData.getInstance();

        List<User> allUsers = new ArrayList<>(fakeData.getFakeUsers());
        List<User> expectedFollowers = allUsers.subList(0, limit);


        GetFollowersRequest request = new GetFollowersRequest(authToken, followerAlias, limit, lastFollowerAlias);
        GetFollowersResponse mockResponse = new GetFollowersResponse(expectedFollowers, true);

        when(clientCommunicatorMock.doPost(anyString(), eq(request), any(), eq(GetFollowersResponse.class)))
                .thenReturn(mockResponse);

        GetFollowersResponse response = serverFacade.getFollowers(request, URL_PATH);

        assertTrue(response.isSuccess());
        assertEquals(expectedFollowers.size(), response.getFollowers().size());
        assertEquals(expectedFollowers, response.getFollowers());
    }





    @Test
    public void testGetFollowers_returnIncorrectFollowers() throws IOException, TweeterRemoteException {

        FakeData fakeData = FakeData.getInstance();

        List<User> allUsers = new ArrayList<>(fakeData.getFakeUsers());
        List<User> incorrectFollowers = new ArrayList<>();


        GetFollowersRequest request = new GetFollowersRequest(authToken, followerAlias, limit, lastFollowerAlias);

        GetFollowersResponse mockResponse = new GetFollowersResponse(incorrectFollowers, false);

        when(clientCommunicatorMock.doPost(anyString(), eq(request), any(), eq(GetFollowersResponse.class)))
                .thenReturn(mockResponse);

        GetFollowersResponse response = serverFacade.getFollowers(request, URL_PATH);


        assertTrue(response.getHasMorePages());
        assertNotEquals(allUsers, response.getFollowers());
    }




}
