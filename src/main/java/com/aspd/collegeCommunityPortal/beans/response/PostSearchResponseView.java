package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchResponseView {
    private Integer id;
    private String title;
    private String fullName;
    private String username;
    private Integer userId;
    private String creationDate;
    private Integer profileImageId;

}
