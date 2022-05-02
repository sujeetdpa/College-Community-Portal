package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;

    @NotBlank(message = "First Name cannot be blank")
    @NotNull(message = "First Name cannot be null")
    private String firstName;

    private String lastName;
    @NotNull(message = "Gender cannot be null")
    private String gender;
    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Invalid DOB")
    private LocalDate dob;

    @NotNull(message = "Mobile No. cannot be null")
    @Size(min = 10,max = 10, message = "Invalid Mobile number length")
    private String mobileNo;
}
