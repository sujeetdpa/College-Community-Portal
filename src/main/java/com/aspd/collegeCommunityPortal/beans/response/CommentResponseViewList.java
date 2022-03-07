package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentResponseViewList {
    private List<CommentResponseView> commentResponseViews;
    private int pageNo;
    private long totalNoOfComment;
    private int totalPages;
}
