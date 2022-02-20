package com.aspd.collegeCommunityPortal.data.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreatePostRequest {
    private String title;
    private String description;
    private List<MultipartFile> files;
    //More fields to come here;
}
