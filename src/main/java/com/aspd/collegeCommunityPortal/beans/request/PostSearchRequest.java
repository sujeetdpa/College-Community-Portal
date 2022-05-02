package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PostSearchRequest {
    @NotBlank(message = "Title cannot be blank")
    @NotNull(message = "Title null")
    private String title;

    private int pageNo;
    private int maxItems;
}
