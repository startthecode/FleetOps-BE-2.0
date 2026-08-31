package com.samtar.userservice.config;

import com.samtar.dto.ExceptionApiResponse;
import com.samtar.exception.TokenExceptions;
import com.samtar.userservice.cache.AuthSessionValidation;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.constants.Routes;
import com.samtar.userservice.dto.common.JwtClaimsDto;
import com.samtar.userservice.enums.TokenTypes;
import com.samtar.userservice.service.imp.UserDetailServiceImp;
import com.samtar.userservice.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilterChain extends OncePerRequestFilter {
    private final UserDetailServiceImp userDetailServiceImp;
    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> unProtectedRoutes = List.of(Routes.unprotected);
    private final ObjectMapper mapper;
    private final AuthSessionValidation authSessionValidation;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER,
                        HttpStatus.UNAUTHORIZED);
            }
            System.out.println(authorization);

            String accessToken = authorization.substring(7);
            if (accessToken.isEmpty())
                throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED);
            JwtClaimsDto decodedToken = jwtUtils.decodeToken(accessToken, TokenTypes.ACCESS_TOKEN);
            if (decodedToken.username() == null || decodedToken.userRole() == null)
                throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED);
            if (!Boolean.TRUE.equals(authSessionValidation.validateSession(decodedToken.sessionId()))) {
                throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER, HttpStatus.UNAUTHORIZED);
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    decodedToken.username(), null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            request.setAttribute("x-userid",decodedToken.userId());
            request.setAttribute("x-sessionid",decodedToken.sessionId());
            request.setAttribute("x-user-role",decodedToken.userRole());
            request.setAttribute("x-user-email",decodedToken.userRole());
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
        if (exception instanceof TokenExceptions) {
            ExceptionApiResponse<String> customResp = new ExceptionApiResponse<>(exception.getMessage(), null, LocalDateTime.now());
            mapper.writeValue(response.getWriter(), customResp);
        } else {
            ExceptionApiResponse<String> customResp = new ExceptionApiResponse<>(MessageConstant.UNAUTHORIZED_USER, null, LocalDateTime.now());
            mapper.writeValue(response.getWriter(), customResp);
        }

    }
}
