package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusDAO {







//    public Pair<List<Status>, Boolean> getStory(String followerAlias, int limit, String lastFolloweeAlias) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert limit > 0;
//        assert followerAlias != null;
//
//        List<User> allFollowees = getDummyFollowees();
//        List<User> responseFollowees = new ArrayList<>(limit);
//
//        boolean hasMorePages = false;
//
//        if(limit > 0) {
//            if (allFollowees != null) {
//                int followeesIndex = getFolloweesStartingIndex(lastFolloweeAlias, allFollowees);
//
//                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < limit; followeesIndex++, limitCounter++) {
//                    responseFollowees.add(allFollowees.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowees.size();
//            }
//        }
//
//        return new Pair<>(responseFollowees, hasMorePages);
//    }
//
//    public Pair<List<User>, Boolean> getFollowers(String followerAlias, int limit, String lastFollowerAlias) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert limit > 0;
//        assert followerAlias != null;
//
//        List<User> allFollowees = getDummyFollowees();
//        List<User> responseFollowees = new ArrayList<>(limit);
//
//        boolean hasMorePages = false;
//
//        if(limit > 0) {
//            if (allFollowees != null) {
//                int followeesIndex = getFolloweesStartingIndex(lastFollowerAlias, allFollowees);
//
//                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < limit; followeesIndex++, limitCounter++) {
//                    responseFollowees.add(allFollowees.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowees.size();
//            }
//        }
//
//        return new Pair<>(responseFollowees, hasMorePages);
//    }





//    /**
//     * Determines the index for the first followee in the specified 'allFollowees' list that should
//     * be returned in the current request. This will be the index of the next followee after the
//     * specified 'lastFollowee'.
//     *
//     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
//     *                          request or null if there was no previous request.
//     * @param allFollowees the generated list of followees from which we are returning paged results.
//     * @return the index of the first followee to be returned.
//     */
//    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {
//
//        int followeesIndex = 0;
//
//        if(lastFolloweeAlias != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < allFollowees.size(); i++) {
//                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    followeesIndex = i + 1;
//                    break;
//                }
//            }
//        }
//
//        return followeesIndex;
//    }


    public Pair<List<Status>, Boolean> getPageOfStatus(Status lastStatus, int limit) {

        Pair<List<Status>, Boolean> result = new Pair<>(new ArrayList<>(), false);

        int index = 0;
        List<Status> fakeStatuses = getDummyStatuses();

        if (lastStatus != null) {
            for (int i = 0; i < fakeStatuses.size(); ++i) {
                Status curStatus = fakeStatuses.get(i);
                if (curStatus.getUser().getAlias().equals(lastStatus.getUser().getAlias()) &&
                        curStatus.getTimestamp().equals(lastStatus.getTimestamp())) {
                    index = i + 1;
                    break;
                }
            }
        }

        for (int count = 0; index < fakeStatuses.size() && count < limit; ++count, ++index) {
            Status curStatus = fakeStatuses.get(index);
            result.getFirst().add(curStatus);
        }

        result.setSecond(index < fakeStatuses.size());

        return result;
    }


    List<Status> getDummyFeed(){
        return getFakeData().getFakeStatuses();
    }

    List<Status> getDummyStatuses(){
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }



}
