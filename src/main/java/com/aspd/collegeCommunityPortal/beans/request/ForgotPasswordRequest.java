package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
public class ForgotPasswordRequest {
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "username cannot be blank")
    @Email
    private String username;

    @NotNull(message = "DOB cannot be null")
    @Past(message = "Invalid Dob")
    private LocalDate dob;
}
