package com.aspd.collegeCommunityPortal.data.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostSearchResponseViewList {
    private List<PostSearchResponseView> postSearchResponseViews;
    private int pageNo;
    private int totalPages;
    private int totalPosts;
    private int maxPosts;
}
