package com.aspd.collegeCommunityPortal.data.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseView {
    private String description;
    private String title;
    private LocalDateTime creationDate;
    private String user;
    private int userId;
    private int noOfLikes;
    private int noOfComments;
}
