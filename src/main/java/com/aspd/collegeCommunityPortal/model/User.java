package com.aspd.collegeCommunityPortal.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String firstName;

//    @Column(nullable = true)
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Boolean isActive;
    private LocalDate dob;
    private LocalDateTime userCreationTimestamp;
    private LocalDateTime lastLoginTimestamp;
    private LocalDateTime currentLoginTimeStamp;
    private LocalDateTime lastLogoutTimestamp;
    private LocalDateTime currentLogoutTimestamp;
    private Integer profileImageId;
    private Boolean isNotLocked;

    @Column(unique = true)
    private String universityId;

    @Column(unique = true)
    private String mobileNo;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles=new ArrayList<>();

    public String getFullName(){
        return this.getFirstName().concat(" ").concat(this.getLastName());
    }


}
