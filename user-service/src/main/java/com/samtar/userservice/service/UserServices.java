package com.samtar.userservice.service;

import com.samtar.consts.CacheKeys;
import com.samtar.dto.cache.SessionCache;
import com.samtar.enums.ROLE;
import com.samtar.exception.BaseException;
import com.samtar.exception.SessionException;
import com.samtar.exception.ValidationException;
import com.samtar.userservice.cache.CacheService;
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
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
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
    record Tokens(String accessToken, String refreshToken) {
    }

    ;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final Long accessExpiry;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final int maxSessionAttempts;
    private final CacheService cacheService;

    public UserServices(AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils,
                        @Value("${app.security.jwt.access-expiry}") Long accessExpiry,
                        PasswordEncoder passwordEncoder,
                        UserRepository userRepository,
                        SessionRepository sessionRepository,
                        CacheService cacheService,
                        @Value("${app.security.authentication.max-session}") int maxSessionAttempts) {
        this.accessExpiry = accessExpiry;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.maxSessionAttempts = maxSessionAttempts;
        this.cacheService = cacheService;
    }

    // signin
    @Transactional
    public SignInRespDto signin(SignInReqDto req) {
        try {
            Authentication userServices = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );
            UserDetailsImp userDetails = (UserDetailsImp) userServices.getPrincipal();
            assert userDetails != null;
            UsersEntity usersEntity = userDetails.getUsers();
            String sessionId = createSession(usersEntity, true);
            Tokens tokens = generateTokens(usersEntity, sessionId);
            createCacheSession(usersEntity, sessionId);
            return new SignInRespDto(
                    tokens.accessToken,
                    tokens.refreshToken,
                    usersEntity.getUsername(),
                    usersEntity.getRole(),
                    accessExpiry
            );
        } catch (BadCredentialsException e) {
            throw new ValidationException(MessageConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED, null);
        } catch (DisabledException e) {
            throw new ValidationException(MessageConstant.ACCOUNT_DISABLED, HttpStatus.FORBIDDEN, null);
        } catch (LockedException e) {
            throw new ValidationException(MessageConstant.ACCOUNT_LOCKED, HttpStatus.FORBIDDEN, null);
        }
    }

    // signup
    @Transactional
    public SignUpResDto signUp(SignUpReqDto req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ValidationException(MessageConstant.INVALID_PAYLOAD, HttpStatus.NOT_ACCEPTABLE, new HashMap<String, String>(Map.of("username", MessageConstant.USERNAME_ALREADY_EXISTS)));
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new ValidationException(MessageConstant.INVALID_PAYLOAD, HttpStatus.NOT_ACCEPTABLE, new HashMap<String, String>(Map.of("email", MessageConstant.EMAIL_ALREADY_EXISTS)));
        }
        UsersEntity newUser = new UsersEntity();
        newUser.setPassword(passwordEncoder.encode(req.password()));
        newUser.setEmail(req.email());
        newUser.setUsername(req.username());
        if (Boolean.TRUE.equals(req.isVendorCreation())) {
            newUser.setRole(ROLE.VENDOR);
        } else {
            newUser.setRole(ROLE.USER);
        }

        userRepository.save(newUser);
        String sessionId = createSession(newUser, false);
        Tokens tokens = generateTokens(newUser, sessionId);
        createCacheSession(newUser, sessionId);
        return new SignUpResDto(
                tokens.accessToken,
                tokens.refreshToken,
                newUser.getUsername(),
                newUser.getRole(),
                accessExpiry
        );

    }

    private Tokens generateTokens(UsersEntity users, String sessionId) {
        JwtClaimsDto jwtClaimsDto = new JwtClaimsDto(users.getUsername(), users.getRole(), users.getId().toString(),sessionId);
        return new Tokens(
                jwtUtils.generateToken(TokenTypes.ACCESS_TOKEN, jwtClaimsDto),
                jwtUtils.generateToken(TokenTypes.REFRESH_TOKEN, jwtClaimsDto)
        );
    }

    @Transactional
    public void logout(String sessionId) {
        try {
            sessionRepository.deleteById(UUID.fromString(sessionId));
            String suffix = CacheKeys.USER_SESSION.toString() + ":" + sessionId;
            if (!cacheService.delete(suffix)) {
                throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void logoutAll(String userid) {
        try {
            List<SessionEntity> allSessions = sessionRepository.findByUser_id(UUID.fromString(userid));
            sessionRepository.deleteAll(allSessions);
            allSessions.forEach(e->{
            String suffix = CacheKeys.USER_SESSION.toString() + ":" + e.getId();
            cacheService.delete(suffix);
            });
        } catch (Exception ex) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void logoutAllExceptCurrent(String sessionId,String userId) {
        try {
            UUID currentSessionId = UUID.fromString(sessionId);
            List<SessionEntity> allSessions = sessionRepository.findByUser_id(UUID.fromString(userId)).stream()
                    .filter(e -> !e.getId().equals(currentSessionId))
                    .toList();
            sessionRepository.deleteAll(allSessions);
            allSessions.forEach(e->{
                String suffix = CacheKeys.USER_SESSION.toString() + ":" + e.getId();
                cacheService.delete(suffix);
            });
        } catch (Exception ex) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public SignInRespDto refreshToken(String refreshToken) {
        try {
          JwtClaimsDto jwtClaimsDto = jwtUtils.decodeToken(refreshToken,TokenTypes.REFRESH_TOKEN);
            UsersEntity usersEntity = userRepository.findById(UUID.fromString(jwtClaimsDto.userId())).orElseThrow(()->new SessionException(MessageConstant.UNAUTHORIZED_USER,HttpStatus.UNAUTHORIZED));
            Tokens tokens = generateTokens(usersEntity, jwtClaimsDto.sessionId());
            createCacheSession(usersEntity, jwtClaimsDto.sessionId());
            return new SignInRespDto(
                    tokens.accessToken,
                    tokens.refreshToken,
                    usersEntity.getUsername(),
                    usersEntity.getRole(),
                    accessExpiry
            );
        } catch (ExpiredJwtException e) {
            throw new SessionException(MessageConstant.SESSION_EXPIRED, HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    private String createSession(UsersEntity users, Boolean isSignIn) {
        if (isSignIn && users.getSessions().size() == maxSessionAttempts) {
            throw new ValidationException(MessageConstant.INVALID_PAYLOAD,
                    HttpStatus.UNAUTHORIZED,
                    new HashMap<String, String>(
                            Map.of("session", MessageConstant.SESSION_LIMIT_REACHED)));
        }
        SessionEntity session = new SessionEntity();
        session.setUser(users);
        session.setIpAddress("128.1.1.1");
        ;
        sessionRepository.save(session);

        return session.getId().toString();
    }

    private void createCacheSession(UsersEntity users, String sessionID) {
        SessionCache cacheForSession = new SessionCache(
                users.getUsername(),
                users.getEmail(),
                users.getRole(),
                users.getId()
        );
        String suffix = CacheKeys.USER_SESSION.toString() + ":" + sessionID;
        boolean setCache = cacheService.set(suffix, cacheForSession);
        if (!setCache) throw new SessionException(
                MessageConstant.FAIL_TO_EXECUTE,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


}



