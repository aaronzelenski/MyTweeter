package edu.byu.cs.tweeter.server.dao;



import static edu.byu.cs.tweeter.server.service.UserService.hashPassword;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.service.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.service.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.service.response.RegisterResponse;
import edu.byu.cs.tweeter.server.lambda.RegisterHandler;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.logging.Logger;


public class UserDAO implements IUserDAO {

    private static final String TableName = "users";
    private static final String BUCKET_NAME = "tweeterprofileimagebucket";
    private static AmazonS3 s3Client = null;
    public final DynamoDbTable<User> userTable;
    private static final Logger logger = Logger.getLogger(RegisterHandler.class.getName());


    public UserDAO() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.userTable = enhancedClient.table(TableName, TableSchema.fromBean(User.class));
        initializeS3Client();
    }
    private static void initializeS3Client() {
        if (s3Client == null) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_2)
                    .build();
        }
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {

        try {
            Key key = Key.builder()
                    .partitionValue(request.getUsername())
                    .build();
            User existingUser = userTable.getItem(r -> r.key(key));

            if (existingUser != null) {
                throw new RuntimeException("[Bad Request] User already exists");
            }

            String imageUrl = request.getImageUrl();

            User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageUrl, hashPassword(request.getPassword()));
            userTable.putItem(user);

            return new RegisterResponse(user, new AuthToken());
        } catch (RuntimeException e) {
            throw new RuntimeException("[Bad Request] " + e.getMessage());
        }
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        Key key = Key.builder()
                .partitionValue(request.getUsername())
                .build();
        User user = userTable.getItem(r -> r.key(key));

        if (user == null) {
            throw new RuntimeException("[Bad Request] User not found");
        }

        String hashedPassword = hashPassword(request.getPassword());
        if (!hashedPassword.equals(user.getPassword())) {
            throw new RuntimeException("[Bad Request] Invalid password");
        }


        return new LoginResponse(user, new AuthToken());
    }








    @Override
    public String uploadImageToS3(String imageString, String alias) {
        try {
            byte[] byteArray = Base64.getDecoder().decode(imageString);

            ObjectMetadata data = new ObjectMetadata();
            data.setContentLength(byteArray.length);
            data.setContentType("image/jpg"); // Adjust if supporting other formats

            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, alias, new ByteArrayInputStream(byteArray), data)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(request);

            return s3Client.getUrl(BUCKET_NAME, alias).toString();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("[Bad Request] Image is not a valid Base64 string");
        }
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {

        Key key = Key.builder()
                .partitionValue(request.getAlias())
                .build();

        User user = userTable.getItem(r -> r.key(key));
        if (user == null) {
            throw new RuntimeException("[Bad Request] User not found");
        }
        logger.info(new GetUserResponse(user) + " is the response ( this is inside my UserDAO::getUser)");
        return new GetUserResponse(user);
    }


    @Override
    public void deleteAllUsers() {
        // Scan the table to get all items (users)
        Iterator<User> iterator = userTable.scan(ScanEnhancedRequest.builder().build()).items().iterator();

        // Iterate through the items and delete each one
        while (iterator.hasNext()) {
            User user = iterator.next();
            Key key = Key.builder()
                    .partitionValue(user.getAlias())
                    .build();

            DeleteItemEnhancedRequest deleteRequest = DeleteItemEnhancedRequest.builder()
                    .key(key)
                    .build();

            userTable.deleteItem(deleteRequest);
        }
    }

}
