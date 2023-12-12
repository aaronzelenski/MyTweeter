package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DynamoDBFactoryDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IFactoryDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;



public class Filler {

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 1000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@1000";


    public static void main(String[] args) {
      // fillDatabase();
        // emptyDatabase();
    }


//    public static void emptyDatabase() {
//
//        IFactoryDAO factoryDAO = new DynamoDBFactoryDAO();
//        IStatusDAO statusDAO = factoryDAO.getStatusDAO();
//
//        statusDAO.deleteAllFeeds();
//    }


    public static void fillDatabase() {

        IFactoryDAO factoryDAO = new DynamoDBFactoryDAO();
        IFollowDAO followDAO = factoryDAO.getFollowDAO();
        IUserDAO userDAO = factoryDAO.getUserDAO();


        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String name = "1000Follower " + i;
            String alias = "@1000_follower" + i;

            // 7268

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User();
            user.setAlias(alias);
            user.setFirstName(name);
            user.setLastName("LastName");
            user.setImageUrl("https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");


            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowBatch(followers, FOLLOW_TARGET);
        }
    }
}