package com.aspd.collegeCommunityPortal.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    private Gender gender;
    private Boolean isActive;
    private LocalDateTime userCreationTimestamp;
    private LocalDateTime lastLoginTimestamp;
    private LocalDateTime lastLogoutTimestamp;
    private String profileImage;
    private Boolean isNotBlocked;

    @Column(unique = true)
    private String mobileNo;
    @Column(unique = true)
    private String universityId;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles;

    public String getFullName(){
        return this.getFirstName().concat(" ").concat(this.getLastName());
    }


}
