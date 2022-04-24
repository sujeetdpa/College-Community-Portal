package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AddAdminRequest {
    private String firstName;
    private String lastName;
    private String username;
    private List<Integer> roles;
}
