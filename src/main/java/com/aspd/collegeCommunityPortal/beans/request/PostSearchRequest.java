package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchRequest {
    private String title;
    private int pageNo;
    private int maxItemsPerPage;
}
