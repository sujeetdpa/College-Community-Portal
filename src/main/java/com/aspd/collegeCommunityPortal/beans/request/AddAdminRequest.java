package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@Setter
public class AddAdminRequest {
    @NotBlank(message = "First Name cannot be blank")
    @NotNull(message = "First Name cannot be null")
    private String firstName;

    private String lastName;

    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be null")
    @Email(message = "Invalid Email")
    private String username;

    @NotNull(message = "Select at least one role")
    private List<Integer> roles;

    @NotNull(message = "Gender cannot be null")
    private String gender;
}
