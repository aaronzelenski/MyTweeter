package edu.byu.cs.tweeter.server.dao;



import static edu.byu.cs.tweeter.server.service.UserService.hashPassword;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.util.ArrayList;
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
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


public class UserDAO extends BaseDAO implements IUserDAO {

    private static final String TableName = "users";
    private static final Logger logger = Logger.getLogger(RegisterHandler.class.getName());


    public UserDAO() {
        super();
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


    public void addUserBatch(List<User> users) {
        List<User> batchToWrite = new ArrayList<>();
        for (User u : users) {
            batchToWrite.add(u);

            if (batchToWrite.size() == 10) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }

    @Override
    public List<User> getUserList(List<String> aliases) {
        List<User> users = new ArrayList<>();
        for (String alias : aliases) {
            Key key = Key.builder()
                    .partitionValue(alias)
                    .build();

            User user = userTable.getItem(r -> r.key(key));
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    private void writeChunkOfUserDTOs(List<User> users) {
        if(users.size() > 10)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<User> table = enhancedClient.table(TableName, TableSchema.fromBean(User.class));
        WriteBatch.Builder<User> writeBuilder = WriteBatch.builder(User.class).mappedTableResource(table);
        for (User item : users) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
