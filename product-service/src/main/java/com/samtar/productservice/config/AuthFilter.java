package com.samtar.productservice.config;

import com.samtar.consts.ReqHeadersKeys;
import com.samtar.dto.ExceptionApiResponse;
import com.samtar.exception.SessionException;
import com.samtar.exception.TokenExceptions;
import com.samtar.productservice.constants.MessageConstant;
import com.samtar.productservice.constants.Routes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> unProtectedRoutes = List.of(Routes.unprotected);
    ;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String userID = request.getHeader(ReqHeadersKeys.USER_ID);
            String role = request.getHeader(ReqHeadersKeys.USER_ROLE);
            if (userID == null || role == null) {
                throw new SessionException(MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED);
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(userID, null, List.of(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            exceptionHandling(request, response, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String reqRoutes = request.getServletPath();
        return unProtectedRoutes.stream().anyMatch(e -> pathMatcher.match(e, reqRoutes));
    }

    private void exceptionHandling(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (exception instanceof SessionException) {
            ExceptionApiResponse<String> customResp = new ExceptionApiResponse<>(exception.getMessage(), null, LocalDateTime.now());
            mapper.writeValue(response.getWriter(), customResp);
        } else {
            ExceptionApiResponse<String> customResp = new ExceptionApiResponse<>(MessageConstant.UNAUTHORIZED_USER, null, LocalDateTime.now());
            mapper.writeValue(response.getWriter(), customResp);
        }

    }
}
