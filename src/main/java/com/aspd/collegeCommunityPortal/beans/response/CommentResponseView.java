package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseView {
    private Integer id;
    private String description;
    private String title;
    private Integer postId;
    private Integer userId;
    private Integer profileImageId;
    private String fullName;
    private String commentDate;
}
