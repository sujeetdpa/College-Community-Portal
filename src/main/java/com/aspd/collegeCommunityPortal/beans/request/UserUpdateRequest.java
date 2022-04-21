package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private String mobileNo;
}
