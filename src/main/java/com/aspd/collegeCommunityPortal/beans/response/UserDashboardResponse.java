package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDashboardResponse {
    private Integer numberOfPosts;
    private Integer numberOfCommentsMade;
    private Integer numberOfLikesMade;
    private Integer numberOfDislikedMade;
    private Integer numberOfLikesAchieved;
    private Integer numberOfDislikesAchieved;
    private Integer numberOfCommentsAchieved;
    private Integer numberOfImages;
    private Integer numberOfDocuments;

}
