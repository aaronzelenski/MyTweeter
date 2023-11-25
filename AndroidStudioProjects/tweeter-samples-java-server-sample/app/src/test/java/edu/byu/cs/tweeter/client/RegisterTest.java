package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ClientCommunicator;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.service.response.RegisterResponse;
import static org.mockito.Mockito.*;

public class RegisterTest {

    private ClientCommunicator clientCommunicatorMock;
    private ServerFacade serverFacade;
    private final String username = "@allen";
    private final String password = "password";
    private  final String firstName = "First name";
    private final String lastName = "Last name";
    private final String imageUrl = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private final String urlPath = "/register";

    @BeforeEach
    public void setUp() {
        clientCommunicatorMock = mock(ClientCommunicator.class);
        serverFacade = new ServerFacade();
    }


    @Test
    public void testSuccessfulRegistration() throws IOException, TweeterRemoteException {
        // Arrange
        String expectedUsername = "@allen";
        AuthToken authtoken = new AuthToken();

        User user = new User(firstName, lastName, username, imageUrl);


        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, username, password, imageUrl);
        RegisterResponse mockResponse = new RegisterResponse(user, authtoken);
        // Mocking clientCommunicator's response if necessary
        // Assuming clientCommunicator is a mock or you have a way to simulate its behavior
        when(clientCommunicatorMock.doPost(urlPath, registerRequest, null, RegisterResponse.class))
                .thenReturn(mockResponse);


        // Act
        RegisterResponse response = serverFacade.register(registerRequest, urlPath);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getUser());
        assertEquals(expectedUsername, response.getUser().getAlias());
        // Additional assertions as needed
    }


    @Test
    public void testInvalidRegistrationData() throws IOException, TweeterRemoteException {
        // Arrange
        String invalidUsername = null; // or other invalid data
        AuthToken authtoken = new AuthToken();

        User user = new User(firstName, lastName, username, imageUrl);

        // Mocking clientCommunicator's response to simulate invalid data handling


        // Act
        RegisterRequest registerRequest = new RegisterRequest(invalidUsername, lastName, username, password, imageUrl);
        RegisterResponse mockResponse = new RegisterResponse(user, authtoken);


        when(clientCommunicatorMock.doPost(urlPath, registerRequest, null, RegisterResponse.class))
                .thenReturn(mockResponse);

        RegisterResponse response = serverFacade.register(registerRequest, urlPath);

        assertFalse(response.isSuccess());
        assertNull(response.getUser());
    }



}
