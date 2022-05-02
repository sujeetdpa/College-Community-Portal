package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank(message = "current Password cannot be null")
    @NotNull(message = "current Password cannot be null")
    private String currentPassword;

    @NotBlank(message = "new Password cannot be null")
    @NotNull(message = "new Password cannot be null")
    private String newPassword;

    @NotNull(message = "Confirm Password cannot be null")
    @NotBlank(message = "Confirm Password cannot be null")
    private String cnfNewPassword;
}
