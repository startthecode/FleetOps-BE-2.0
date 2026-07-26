package com.samtar.userservice.controller;

import com.samtar.dto.SuccessApiResponse;
import com.samtar.exception.SessionException;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.dto.request.SignInReqDto;
import com.samtar.userservice.dto.request.SignUpReqDto;
import com.samtar.userservice.dto.response.SignInRespDto;
import com.samtar.userservice.dto.response.SignUpResDto;
import com.samtar.userservice.service.UserServices;
import com.samtar.userservice.shared.AuthCookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class UsersController {
    private final UserServices userServices;
    private final AuthCookieUtil authCookieUtil;
    private final String cookieName;

    public UsersController(@Value("${app.security.cookie.auth-token.name}") String cookieName, UserServices userServices, AuthCookieUtil authCookieUtil) {
        this.cookieName = cookieName;
        this.userServices = userServices;
        this.authCookieUtil = authCookieUtil;
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<SuccessApiResponse<SignInRespDto>> signIn(@Valid @RequestBody SignInReqDto req,
                                                                    HttpServletResponse response) {
        SignInRespDto responseData = userServices.signin(req);
        SuccessApiResponse<SignInRespDto> resp = new SuccessApiResponse<>(MessageConstant.USER_SIGNIN, responseData,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.addAuthTokenCookie(responseData.refreshToken()));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<SuccessApiResponse<SignUpResDto>> signUp(@Valid @RequestBody SignUpReqDto req,
                                                                   HttpServletResponse response) {
        SignUpResDto responseData = userServices.signUp(req);
        SuccessApiResponse<SignUpResDto> resp = new SuccessApiResponse<>(MessageConstant.USER_CREATED, responseData,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.addAuthTokenCookie(responseData.refreshToken()));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessApiResponse<SignUpResDto>> logoutCurrent(HttpServletRequest request, HttpServletResponse response) {
        String sessionID = (String) request.getAttribute("x-sessionid");
        userServices.logout(sessionID);
        SuccessApiResponse<SignUpResDto> resp = new SuccessApiResponse<>(MessageConstant.USER_LOGOUT, null,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.removeAuthTokenCookie());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<SuccessApiResponse<SignUpResDto>> logoutAll(HttpServletRequest request, HttpServletResponse response) {
        String userID = (String) request.getAttribute("x-userid");
        userServices.logoutAll(userID);
        SuccessApiResponse<SignUpResDto> resp = new SuccessApiResponse<>(MessageConstant.USER_LOGOUT, null,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.removeAuthTokenCookie());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout-except")
    public ResponseEntity<SuccessApiResponse<SignUpResDto>> logoutAllExceptCurrent(HttpServletRequest request, HttpServletResponse response) {
        String sessionID = (String) request.getAttribute("x-sessionid");
        String userId = (String) request.getAttribute("x-userid");
        userServices.logoutAllExceptCurrent(sessionID, userId);
        SuccessApiResponse<SignUpResDto> resp = new SuccessApiResponse<>(MessageConstant.USER_LOGOUT, null,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.removeAuthTokenCookie());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<SuccessApiResponse<SignInRespDto>> refreshToken(HttpServletRequest request,
                                                                          HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            throw new SessionException(MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED);
        }
       Cookie refreshToken =  Arrays.stream(cookies).filter(e -> Objects.equals(e.getName(), cookieName)).findFirst().orElseThrow(() -> new SessionException(MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED));

        System.out.println(refreshToken.getValue());
        SignInRespDto responseData = userServices.refreshToken(refreshToken.getValue());
        SuccessApiResponse<SignInRespDto> resp = new SuccessApiResponse<>(MessageConstant.REFRESH_TOKEN_GENERATED, responseData,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.addAuthTokenCookie(responseData.refreshToken()));
        return ResponseEntity.ok(resp);
    }


}
