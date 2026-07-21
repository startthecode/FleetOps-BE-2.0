package com.samtar.userservice.entity;


import java.util.ArrayList;
import java.util.List;

import com.samtar.enums.ROLE;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.constants.RegexConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "user_table")
public class UsersEntity extends BaseEntity {

    @NotBlank(message = "Username :" + MessageConstant.REQUIRED)
    @Column(nullable = false, unique = true)
    @Pattern(
            regexp = RegexConstant.USERNAME,
            message = MessageConstant.INVALID_USERNAME
    )
    private String username;

    @NotBlank(message = "Email :" + MessageConstant.REQUIRED)
    @Column(nullable = false, unique = true)
    @Pattern(
            regexp = RegexConstant.EMAIL,
            message = MessageConstant.INVALID_EMAIL
    )
    private String email;

    @NotBlank(message = "Password :" + MessageConstant.REQUIRED)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ROLE role = ROLE.USER;

    //Connection with session entity
    @OneToMany(mappedBy="user",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionEntity> sessions = new ArrayList<>();
}
