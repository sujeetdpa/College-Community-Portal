package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikePostResponse {
    private int noOfLikes;
    private int postId;
    private int userId;
    private String message;
}
