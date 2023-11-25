//package edu.byu.cs.tweeter.client;
//
//import static org.mockito.Mockito.verify;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//
//import java.util.Arrays;
//import java.util.List;
//
//import edu.byu.cs.tweeter.client.cache.Cache;
//import edu.byu.cs.tweeter.client.model.service.StatusService;
//import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
//import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.Status;
//import edu.byu.cs.tweeter.model.domain.User;
//
//public class PostStatusTest {
//
//    private MainActivityPresenter.View mockView;
//    private StatusService mockStatusService;
//    private Cache mockCache;
//    private MainActivityPresenter mainActivityPresenterSpy;
//    private User testUser;
//
//
//    @BeforeEach
//    public void setup() {
//        mockView = Mockito.mock(MainActivityPresenter.View.class);
//        mockStatusService = Mockito.mock(StatusService.class);
//        mockCache = Mockito.mock(Cache.class);
//
//        testUser = new User("Jeff", "Smith", "@JeffSmith", "imageURL");
//
//        mainActivityPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView, testUser));// Assuming the constructor accepts the service as an argument
//        Mockito.when(mainActivityPresenterSpy.createService()).thenReturn(mockStatusService);
//
//        Cache.setInstance(mockCache);
//    }
//
//    @Test
//    public void testPostStatus_postStatusSucceeds(){
//
//
//        AuthToken authToken = new AuthToken("1");
//        Status newStatus = new Status("test status", testUser, 0, Arrays.asList("url1", "url2"), Arrays.asList("mention1", "mention2"));
//
//        Answer<Void> successAnswer = invocation -> {
//            AuthToken authTokenArg = invocation.getArgument(0);
//            Status statusArg = invocation.getArgument(1);
//
//            if(authTokenArg == authToken || statusArg == newStatus){
//                StatusService.postStatusObserver observer = invocation.getArgument(2);
//                observer.postStatusSucceeded("status success");
//            }
//            return null;
//        };
//
//        Mockito.doAnswer(successAnswer).when(mockStatusService).postStatus(Mockito.any(AuthToken.class),
//                Mockito.any(Status.class), Mockito.any(StatusService.postStatusObserver.class));
//
//
//        mainActivityPresenterSpy.postStatus(authToken, newStatus);
//
//
//        verify(mockView).showInfoMessage("Posting status...");
//        verify(mockView).showSuccessMessage("status success");
//    }
//
//    @Test
//    public void testPostStatus_postStatusFailed(){
//
//        Answer<Void> callHandleFailedAnswer = invocation -> {
//            StatusService.postStatusObserver observer = invocation.getArgument(2);
//            observer.postStatusFailed("Failed to post status: <ERROR MESSAGE>");
//            return null;
//        };
//
//        AuthToken authToken = new AuthToken();
//        Status newStatus = new Status();
//
//
//
//        mainActivityPresenterSpy.postStatus(authToken, newStatus);
//
//        Mockito.doAnswer(callHandleFailedAnswer).when(mockStatusService).postStatus(Mockito.any(AuthToken.class),
//                Mockito.any(Status.class), Mockito.any(StatusService.postStatusObserver.class));
//
//
//        verify(mockView).showInfoMessage("Posting status...");
//        //verify(mockView).hideInfoMessage();
//        //verify(mockView).showErrorMessage("Failed to post status: <ERROR MESSAGE>");
//        //verify(mockView).showSuccessMessage("status success");
//
//
//    }
//
//    @Test
//    public void testPostStatus_postStatusFailsWithExceptionMessage() {
//        Answer<Void> callHandleExceptionAnswer = invocation -> {
//            StatusService.postStatusObserver observer = invocation.getArgument(2);
//            observer.postStatusFailed("Failed to post status because of exception: ‹EXCEPTION MESSAGE>");
//            return null;
//        };
//
//        AuthToken authToken = new AuthToken();
//        authToken = null;
//        Status newStatus = new Status();
//        newStatus = null;
//
//        mainActivityPresenterSpy.postStatus(authToken, newStatus);
//
//        Mockito.doAnswer(callHandleExceptionAnswer).when(mockStatusService).postStatus(Mockito.any(AuthToken.class),
//                Mockito.any(Status.class), Mockito.any(StatusService.postStatusObserver.class));
//
//
//        verify(mockView).showInfoMessage("Posting status...");
//        //verify(mockView).hideInfoMessage();
//        //verify(mockView).showErrorMessage("Failed to post status because of exception: ‹EXCEPTION MESSAGE>");
//        //verify(mockView).showSuccessMessage("status success");
//
//    }
//
//
//
//
//
//
//
//
//
//
//}
