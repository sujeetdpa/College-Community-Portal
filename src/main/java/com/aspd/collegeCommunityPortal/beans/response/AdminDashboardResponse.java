package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardResponse {
    private Long numberOfPosts;
    private Integer numberOfDeletedPost;
    private Integer numberOfDeletedComment;
    private Long numberOfUsers;
    private Integer numberOfAdmins;
    private Long numberOfComments;
    private Integer numberOfLikes;
    private Integer numberOfDislikes;
    private Long numberOfImages;
    private Long numberOfDocuments;
}
