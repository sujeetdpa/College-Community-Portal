package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Integer roleId;
}
