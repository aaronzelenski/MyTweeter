#!/bin/bash
arr=(
    "getStory"
    "register"
    "getFollower"
    "toFollow"
    "getFollowing"
    "logout"
    "getFeed"
    "toUnfollow"
    "isFollower"
    "getUser"
    "getFollowersCount"
    "getFollowingCount"
    "login"
    "postStatus"
)
for FUNCTION_NAME in "${arr[@]}"
do
  aws lambda update-function-code --function-name $FUNCTION_NAME --zip-file fileb:///Users/thisguyaaron/AndroidStudioProjects/tweeter-samples-java-server-sample/server/build/libs/server-all.jar &
done