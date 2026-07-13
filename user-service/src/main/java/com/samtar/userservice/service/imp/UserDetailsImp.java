package com.samtar.userservice.service.imp;

import com.samtar.enums.Status;
import com.samtar.userservice.entity.UsersEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
public class UserDetailsImp implements UserDetails {
    private  final UsersEntity users;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonLocked() {
        return users.getStatus() == Status.ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return users.getStatus() == Status.ACTIVE;
    }

    public UsersEntity getUsers(){
        return users;
    }


}
