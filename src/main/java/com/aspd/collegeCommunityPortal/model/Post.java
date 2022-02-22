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
    private int id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    @ManyToOne
    private User user;
    @OneToMany
    private List<Image> images;
    @OneToMany
    private List<File> files;
    @OneToMany
    private List<Comment> comments;
    @OneToMany
    private List<Review> reviews;


}
