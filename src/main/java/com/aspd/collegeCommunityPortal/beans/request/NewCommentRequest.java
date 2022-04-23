package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentRequest {
    private String title;
    private String description;
    private int postId;
    private int userId;
}