package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserResponseView {
    private String fullName;
    private String firstName;
    private String lastName;
    private Integer id;
    private String gender;
    private String username;
    private String universityId;
    private String userCreationTimestamp;
    private LocalDate dob;
    private String lastLoginTimestamp;
    private String mobileNo;
    private Integer profileImageId;
    private List<String> role;
}
