package com.samtar.userservice.service;

import com.samtar.enums.ROLE;
import com.samtar.exception.ValidationException;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.dto.common.JwtClaimsDto;
import com.samtar.userservice.dto.request.SignInReqDto;
import com.samtar.userservice.dto.request.SignUpReqDto;
import com.samtar.userservice.dto.response.SignInRespDto;
import com.samtar.userservice.dto.response.SignUpResDto;
import com.samtar.userservice.entity.SessionEntity;
import com.samtar.userservice.entity.UsersEntity;
import com.samtar.userservice.enums.TokenTypes;
import com.samtar.userservice.repository.SessionRepository;
import com.samtar.userservice.repository.UserRepository;
import com.samtar.userservice.service.imp.UserDetailsImp;
import com.samtar.userservice.utils.JwtUtils;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
    private final SessionRepository sessionRepository;
    private final int maxSessionAttempts;
    public UserServices(AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils,
                        @Value("${app.security.jwt.access-expiry}") Long accessExpiry,
                        PasswordEncoder passwordEncoder,
                        UserRepository userRepository,
                        SessionRepository sessionRepository,
                        @Value("${app.security.authentication.max-session}") int maxSessionAttempts) {
        this.accessExpiry = accessExpiry;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.maxSessionAttempts = maxSessionAttempts;
    }

    // signin
    @Transactional
    public SignInRespDto signin(SignInReqDto req) {
      try{
          Authentication userServices = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        UserDetailsImp userDetails = (UserDetailsImp) userServices.getPrincipal();
        assert userDetails != null;
        UsersEntity usersEntity = userDetails.getUsers();
        String sessionId = createSession(usersEntity,true);
        Tokens tokens = generateTokens(usersEntity, sessionId);
        return new SignInRespDto(
                tokens.accessToken,
                tokens.refreshToken,
                usersEntity.getUsername(),
                usersEntity.getRole(),
                accessExpiry
        );
      }
      catch(BadCredentialsException e){
        throw new ValidationException(MessageConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED, null);
      }
      catch(DisabledException e){
        throw new ValidationException(MessageConstant.ACCOUNT_DISABLED, HttpStatus.FORBIDDEN, null);
      }
       catch(LockedException e){
        throw new ValidationException(MessageConstant.ACCOUNT_LOCKED, HttpStatus.FORBIDDEN, null);
      }
    }

    // signup
    @Transactional
    public SignUpResDto signUp(SignUpReqDto req) {
        if(userRepository.existsByUsername(req.username())){
            throw new ValidationException(MessageConstant.INVALID_PAYLOAD, HttpStatus.NOT_ACCEPTABLE,new HashMap<String, String>(Map.of("username", MessageConstant.USERNAME_ALREADY_EXISTS)));
        }
       if(userRepository.existsByEmail(req.email())){
            throw new ValidationException(MessageConstant.INVALID_PAYLOAD, HttpStatus.NOT_ACCEPTABLE,new HashMap<String, String>(Map.of("email", MessageConstant.EMAIL_ALREADY_EXISTS)));
        }
        UsersEntity newUser = new UsersEntity();
        newUser.setPassword(passwordEncoder.encode(req.password()));
        newUser.setEmail(req.email());
        newUser.setUsername(req.username());
        if (Boolean.TRUE.equals(req.isVendorCreation())) {
            newUser.setRole(ROLE.VENDOR);
        }else {
            newUser.setRole(ROLE.USER);
        }

        userRepository.save(newUser);
        String sessionId = createSession(newUser,false);
        Tokens tokens = generateTokens(newUser, sessionId);
        return new SignUpResDto(
                tokens.accessToken,
                tokens.refreshToken,
                newUser.getUsername(),
                newUser.getRole(),
                accessExpiry
        );

    }

    private Tokens generateTokens(UsersEntity users, String sessionId) {
        JwtClaimsDto jwtClaimsDto = new JwtClaimsDto(users.getUsername(), users.getRole(),sessionId.toString());
        return new Tokens(
                jwtUtils.generateToken(TokenTypes.ACCESS_TOKEN, jwtClaimsDto),
                jwtUtils.generateToken(TokenTypes.REFRESH_TOKEN, jwtClaimsDto)
        );
    }

    @Transactional
    private String createSession(UsersEntity users,Boolean isSignIn) {
        if(isSignIn && users.getSessions().size() == maxSessionAttempts){
            throw new ValidationException(MessageConstant.INVALID_PAYLOAD,
                 HttpStatus.UNAUTHORIZED,
                 new HashMap<String, String>(
                    Map.of("session", MessageConstant.SESSION_LIMIT_REACHED)));
        }
        SessionEntity session = new SessionEntity();
        session.setUser(users);
        session.setIpAddress("128.1.1.1");;
        sessionRepository.save(session);
        
        return session.getId().toString();
    }

    
}



