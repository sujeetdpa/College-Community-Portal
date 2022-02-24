package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Getter
@Setter
public class CreatePostRequest {
    private String title;
    private String description;
    private Optional<List<MultipartFile>> images;
    private Optional<List<MultipartFile>> documents;
    //More fields to come here;
}
