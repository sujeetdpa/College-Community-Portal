package com.aspd.collegeCommunityPortal.beans.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseView {
    private Integer id;
    private String description;
    private String title;
    private String creationDate;
    private String deleteDate;
    private String fullName;
    private Integer userId;
    private String universityId;
    private Integer profileImageId;
    private Integer noOfLikes;
    private Integer noOfComments;
    private List<ImageResponse> imageResponses;
    private List<DocumentResponse> documentResponses;
}
