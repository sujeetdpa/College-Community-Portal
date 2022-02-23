package com.aspd.collegeCommunityPortal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String description;
    private LocalDateTime commentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
