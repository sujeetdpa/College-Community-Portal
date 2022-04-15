package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseViewList {
    private List<PostResponseView> postResponseViews;
    private int pageNo;
    private int totalPages;
    private long totalNumberOfItems;
    private int maxItems;
}
