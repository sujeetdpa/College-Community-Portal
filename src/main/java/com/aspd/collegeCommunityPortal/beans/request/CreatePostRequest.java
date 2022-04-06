package com.aspd.collegeCommunityPortal.beans.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
public class CreatePostRequest {
    private String title;
    private String description;
    private List<Integer> images;
    private List<Integer> documents;
    //More fields to come here;
}
