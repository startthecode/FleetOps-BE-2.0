package com.samtar.userservice.service;

import com.samtar.enums.ROLE;
import com.samtar.userservice.dto.common.JwtClaimsDto;
import com.samtar.userservice.dto.request.SignInReqDto;
import com.samtar.userservice.dto.request.SignUpReqDto;
import com.samtar.userservice.dto.response.SignInRespDto;
import com.samtar.userservice.dto.response.SignUpResDto;
import com.samtar.userservice.entity.UsersEntity;
import com.samtar.userservice.enums.TokenTypes;
import com.samtar.userservice.repository.UserRepository;
import com.samtar.userservice.service.imp.UserDetailsImp;
import com.samtar.userservice.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class UserServices {
    record Tokens(String accessToken, String refreshToken) {};
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Long accessExpiry;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public UserServices(AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils,
                        @Value("${app.security.jwt.access-expiry}") Long accessExpiry,
                        PasswordEncoder passwordEncoder,
                        UserRepository userRepository) {
        this.accessExpiry = accessExpiry;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // signin
    @Transactional
    public SignInRespDto signin(SignInReqDto req) {
        Authentication userServices = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        UserDetailsImp userDetails = (UserDetailsImp) userServices.getPrincipal();
        assert userDetails != null;
        UsersEntity usersEntity = userDetails.getUsers();
        Tokens tokens = generateTokens(usersEntity);
        return new SignInRespDto(
                tokens.accessToken,
                tokens.refreshToken,
                usersEntity.getUsername(),
                usersEntity.getRole(),
                accessExpiry
        );
    }

    // signup
    @Transactional
    public SignUpResDto signUp(SignUpReqDto req) {
        UsersEntity newUser = new UsersEntity();
        newUser.setPassword(passwordEncoder.encode(req.password()));
        newUser.setEmail(req.email());
        newUser.setUsername(req.username());
        if (req.isVendorCreation()) {
            newUser.setRole(ROLE.VENDOR);
        }else {
            newUser.setRole(ROLE.USER);
        }

        userRepository.save(newUser);
        Tokens tokens = generateTokens(newUser);
        return new SignUpResDto(
                tokens.accessToken,
                tokens.refreshToken,
                newUser.getUsername(),
                newUser.getRole(),
                accessExpiry
        );

    }

    private Tokens generateTokens(UsersEntity users) {
        JwtClaimsDto jwtClaimsDto = new JwtClaimsDto(users.getUsername(), users.getRole());
        return new Tokens(
                jwtUtils.generateToken(TokenTypes.ACCESS_TOKEN, jwtClaimsDto),
                jwtUtils.generateToken(TokenTypes.REFRESH_TOKEN, jwtClaimsDto)
        );
    }

}
