package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikePostResponse {
    private Integer noOfLikes;
    private Integer postId;
    private Integer userId;
    private String message;
}
