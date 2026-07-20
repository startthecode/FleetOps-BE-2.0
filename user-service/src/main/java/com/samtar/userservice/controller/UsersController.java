package com.samtar.userservice.controller;

import com.samtar.dto.SuccessApiResponse;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.dto.request.SignInReqDto;
import com.samtar.userservice.dto.request.SignUpReqDto;
import com.samtar.userservice.dto.response.SignInRespDto;
import com.samtar.userservice.dto.response.SignUpResDto;
import com.samtar.userservice.service.UserServices;
import com.samtar.userservice.shared.AuthCookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UsersController {
    private final UserServices userServices;
    private final AuthCookieUtil authCookieUtil;

    @PostMapping("/signin")
    public ResponseEntity<SuccessApiResponse<SignInRespDto>> signIn(@RequestBody SignInReqDto req,
            HttpServletResponse response) {
        SignInRespDto responseData = userServices.signin(req);
        SuccessApiResponse<SignInRespDto> resp = new SuccessApiResponse<>(MessageConstant.USER_SIGNIN, responseData,
                LocalDateTime.now());
        response.addCookie(authCookieUtil.addAuthTokenCookie(responseData.refreshToken()));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/signup") 
    public ResponseEntity<SuccessApiResponse<SignUpResDto>> signUp(@RequestBody SignUpReqDto req,
            HttpServletResponse response) {
            SignUpResDto responseData = userServices.signUp(req);
        SuccessApiResponse<SignUpResDto> resp = new SuccessApiResponse<>(MessageConstant.USER_CREATED, responseData,
                LocalDateTime.now());
//        response.addCookie(authCookieUtil.addAuthTokenCookie(responseData.refreshToken()));
        return ResponseEntity.ok(resp);
    }
}
