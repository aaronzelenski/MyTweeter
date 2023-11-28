package edu.byu.cs.tweeter.server.dao;

import java.sql.Timestamp;
import java.util.Iterator;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO implements IAuthTokenDAO {
    private static final String TableName = "authtoken";
    private static DynamoDbClient dynamoDbClient = null;
    private final DynamoDbTable<AuthToken> authTokenTable;

    public AuthTokenDAO() {
        if (dynamoDbClient == null) {
            synchronized (AuthTokenDAO.class) {
                if (dynamoDbClient == null) {
                    dynamoDbClient = DynamoDbClient.builder()
                            .region(Region.US_EAST_2)
                            .build();
                }
            }
        }

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.authTokenTable = enhancedClient.table(TableName, TableSchema.fromBean(AuthToken.class));
    }

    @Override
    public String addToken(String token, long timestamp) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty.");
        }

        AuthToken authtoken = new AuthToken(token, timestamp);

        authTokenTable.putItem(authtoken);

        return token;
    }

    @Override
    public AuthToken validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty.");
        }

        Key key = Key.builder()
                .partitionValue(token)
                .build();

        AuthToken authToken = authTokenTable.getItem(key);

        if (authToken != null) {
            long tokenTimestamp = authToken.getTimestamp();
            long currentTime = System.currentTimeMillis();

            if (currentTime - tokenTimestamp < 3600000) { // 3600000 milliseconds in an hour
                return authToken;
            }
        }
        return null;
    }


    public void deleteAllTokens() {
        // Scan the table for all items
        Iterator<AuthToken> iterator = authTokenTable.scan(ScanEnhancedRequest.builder().build()).items().iterator();

        // Iterate through the items and delete each one
        while (iterator.hasNext()) {
            AuthToken token = iterator.next();
            deleteToken(token.getToken());
        }
    }


    //String.valueOf(System.currentTimeMillis());

//    private AuthToken createAndStoreAuthToken(AuthToken token) {
//        token.setToken(UUID.randomUUID().toString());
//        long currTime = System.currentTimeMillis();
//        authDAO.addToken(token.getToken(), String.valueOf(currTime));
//        return token;
//    }




//    public String validateToken(String token) {
//        Key key = Key.builder()
//                .partitionValue(token)
//                .build();
//
//        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
//                .key(key)
//                .build();
//
//        AuthToken authToken = authTokenTable.getItem(request);
//        return authToken != null ? authToken.getCreationTime() : null;
//    }

    public void deleteToken(String token) {
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder()
                .key(key)
                .build();

        authTokenTable.deleteItem(request);
    }
}

