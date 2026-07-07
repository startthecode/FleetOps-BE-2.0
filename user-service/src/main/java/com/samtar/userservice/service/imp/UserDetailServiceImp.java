package com.samtar.userservice.service.imp;

import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {
    private  final UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).map(UserDetailsImp::new).orElseThrow(()-> new UsernameNotFoundException(MessageConstant.USER_NOT_FOUND));
    }
}
