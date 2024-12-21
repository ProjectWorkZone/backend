package com.project.workzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.workzone.model.common.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Audit implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer age;
    private String email;
    private String phoneNumber;
    private String lastName;
    private String firstName;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String accessToken;
    private String refreshToken;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

//    private boolean accountNonExpired;
//    private boolean credentialsNonExpired;
    private boolean isEnabled;

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        BANNED
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
//        return this.accountNonExpired;
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == Status.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return this.credentialsNonExpired;
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

}
