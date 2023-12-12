package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Feed;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class BaseDAO {

    protected DynamoDbClient dynamoDbClient;
    protected DynamoDbEnhancedClient enhancedClient;
    private static final String FeedTableName = "feed";
    private static final String FollowsTableName = "follows";
    private static final String StatusTableName = "status";
    private static final String UsersTableName = "users";

    private static final String AuthTokenTableName = "authtoken";
    public static final String BUCKET_NAME = "tweeterprofileimagebucket";

    public static AmazonS3 s3Client = null;



    public DynamoDbTable<Follow> followDynamoDbTable;
    public DynamoDbTable<Feed> feedDynamoDbTable;
    public DynamoDbTable<User> userTable;
    public DynamoDbTable<AuthToken> authTokenTable;
    public DynamoDbTable<Status> statusTable;



    protected BaseDAO() {
        initializeDynamoDbClient();
        initializeS3Client();
        initializeTables();
    }

    private void initializeDynamoDbClient() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .build();

        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }


    private void initializeS3Client() {
        if (s3Client == null) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_2)
                    .build();
        }
    }

    private void initializeTables() {
        this.followDynamoDbTable = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follow.class));
        this.feedDynamoDbTable = enhancedClient.table(FeedTableName, TableSchema.fromBean(Feed.class));
        this.userTable = enhancedClient.table(UsersTableName, TableSchema.fromBean(User.class));
        this.authTokenTable = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthToken.class));
        this.statusTable = enhancedClient.table(StatusTableName, TableSchema.fromBean(Status.class));
    }

}