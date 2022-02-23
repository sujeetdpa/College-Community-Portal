package com.aspd.collegeCommunityPortal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private LocalDateTime reviewDate;
    @Enumerated(value = EnumType.STRING)
    private ReviewType reviewType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
