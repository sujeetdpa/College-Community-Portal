package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "First Name cannot be blank")
    @NotNull(message = "First Name cannot be null")
    private String firstName;

    private String lastName;

    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be null")
    @Email(message = "Invalid Email")
    private String username;

    @NotBlank(message = "Password cannot be null")
    @NotNull(message = "Password cannot be null")
    private String password;

    @NotNull(message = "Confirm Password cannot be null")
    @NotBlank(message = "Confirm Password cannot be null")
    private String cnfPassword;

    @NotNull(message = "Gender cannot be null")
    private String gender;

    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Invalid DOB")
    private LocalDate dob;

    @NotNull(message = "Mobile No. cannot be null")
    @Size(min = 10,max = 10, message = "Invalid Mobile number length")
    private String mobileNo;
}
