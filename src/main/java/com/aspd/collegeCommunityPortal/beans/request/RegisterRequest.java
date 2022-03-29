package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String gender;
    private LocalDate dob;
    private String mobileNo;
}
