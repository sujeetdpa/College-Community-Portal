package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@Setter
public class CreatePostRequest {
    @NotBlank(message = "Title cannot be blank")
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotBlank(message = "description cannot be blank")
    @NotNull(message = "description cannot be null")
    private String description;

    private List<Integer> images;
    private List<Integer> documents;
}
