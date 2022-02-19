package com.aspd.collegeCommunityPortal.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseViewList {
    private List<PostResponseView> postResponseViews;
    private int pageNo;
    private int totalNoOfPost;
    private int totalPages;
}
