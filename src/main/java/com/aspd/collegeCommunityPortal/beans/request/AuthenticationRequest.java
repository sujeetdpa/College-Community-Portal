package com.aspd.collegeCommunityPortal.beans.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthenticationRequest {
    @NotNull(message = "Username cannot be null")
    @Email(message = "Invalid Email")
    private String username;

    @NotNull(message = "Password cannot be null")
    @NotBlank
    private String password;
}
