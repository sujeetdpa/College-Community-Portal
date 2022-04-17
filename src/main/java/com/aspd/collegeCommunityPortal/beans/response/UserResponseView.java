package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseView {
    private String fullName;
    private Integer id;
    private String username;
    private String universityId;
    private LocalDate dob;
    private LocalDateTime lastLoginTimestamp;
    private String mobileNo;
    private int profileImageId;
}
