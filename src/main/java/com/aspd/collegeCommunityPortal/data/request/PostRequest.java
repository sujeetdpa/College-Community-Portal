package com.aspd.collegeCommunityPortal.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    private int pageNo;
    private String sortBy;
    private int maxPostRequest;
}
