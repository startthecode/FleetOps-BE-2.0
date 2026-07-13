package com.samtar.userservice.service.imp;

import com.samtar.userservice.dto.common.JwtClaimsDto;
import com.samtar.userservice.dto.request.SignInReqDto;
import com.samtar.userservice.dto.response.SignInRespDto;
import com.samtar.userservice.entity.UsersEntity;
import com.samtar.userservice.enums.TokenTypes;
import com.samtar.userservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service

public class UserServices {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Long accessExpiry;
    record Tokens(
            String accessToken,
            String refreshToken
    ) {
    }

    ;


    public UserServices(AuthenticationManager authenticationManager, JwtUtils jwtUtils,@Value("${app.security.access-expiry}") Long accessExpiry) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.accessExpiry = accessExpiry;
    }

    // signin
    public SignInRespDto signin(SignInReqDto req) {
        Authentication userServices = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        UserDetailsImp userDetails = (UserDetailsImp) userServices.getPrincipal();
        assert userDetails != null;
        UsersEntity usersEntity = userDetails.getUsers();
        Tokens tokens = generateTokens(usersEntity);
        SignInRespDto respDto = new SignInRespDto(
                tokens.accessToken,
                tokens.refreshToken,
                usersEntity.getUsername(),
                usersEntity.getRole(),
                    accessExpiry
                );
        return respDto;
    }


    private Tokens generateTokens(UsersEntity users) {
        JwtClaimsDto jwtClaimsDto = new JwtClaimsDto(users.getUsername(), users.getRole());
        return new Tokens(
                jwtUtils.generateToken(TokenTypes.ACCESS_TOKEN, jwtClaimsDto),
                jwtUtils.generateToken(TokenTypes.REFRESH_TOKEN, jwtClaimsDto)
        );
    }

}
