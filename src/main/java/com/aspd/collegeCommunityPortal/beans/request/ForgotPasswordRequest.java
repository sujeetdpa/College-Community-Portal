package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ForgotPasswordRequest {
    private String username;
    private LocalDate dob;
}
