package com.aspd.collegeCommunityPortal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Gender gender;
    private boolean isActive;
    @Column(unique = true)
    private String mobileNo;
    @Column(unique = true)
    private String universityId;
    private LocalDateTime userCreationTimestamp;
    private LocalDateTime lastLoginTimestamp;
    private LocalDateTime lastLogoutTimestamp;
    private String profileImage;

    @ManyToMany
    private Collection<Role> roles;

    public String getFullName(){
        return this.getFirstName().concat(" ").concat(this.getLastName());
    }


}
