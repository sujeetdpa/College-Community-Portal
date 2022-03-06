package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchResponseView {
    private int id;
    private String title;
    private String fullName;
    private String username;
    private int userId;

}
