package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseView {
    private int id;
    private String description;
    private String title;
    private int postId;
    private int userId;
    private int profileImageId;
    private String fullName;
    private LocalDateTime commentDate;
}
