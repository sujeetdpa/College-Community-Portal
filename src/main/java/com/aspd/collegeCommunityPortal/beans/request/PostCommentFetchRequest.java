package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentFetchRequest {
    private int postId;
    private int pageNo;
    private int itemsPerPage;
}
