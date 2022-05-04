package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DislikePostResponse {
    private int noOfDislikes;
    private int postId;
    private int userId;
    private String message;
}
