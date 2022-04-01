package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseView {
    private Integer id;
    private String description;
    private String title;
    private LocalDateTime creationDate;
    private String fullName;
    private Integer userId;
    private Integer noOfLikes;
    private Integer noOfComments;
}
