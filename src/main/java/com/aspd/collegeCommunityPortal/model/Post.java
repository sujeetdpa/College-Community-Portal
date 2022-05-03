package com.aspd.collegeCommunityPortal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String title;
    @Lob
    private String description;
    private LocalDateTime creationDate;
    private Boolean isDeleted=false;
    private LocalDateTime deleteTimestamp;
    @ManyToOne
    private User user;

//    @OneToMany(fetch = FetchType.LAZY)
//    private List<Image> images;
//
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<Document> documents;
//
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<Comment> comments;
//
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<Review> reviews;


}
