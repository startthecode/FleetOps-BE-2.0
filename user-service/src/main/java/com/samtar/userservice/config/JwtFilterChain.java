package com.samtar.userservice.config;

import com.samtar.dto.ExceptionApiResponse;
import com.samtar.exception.TokenExceptions;
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
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.startsWith("Bearer ")){
            throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER);
        }

        String accessToken = authorization.substring(7);
        if(accessToken.isEmpty()) throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER);
        JwtClaimsDto decodedToken = jwtUtils.decodeToken(accessToken, TokenTypes.ACCESS_TOKEN);
        if(decodedToken.username() == null ||  decodedToken.userRole() == null) throw new TokenExceptions(MessageConstant.UNAUTHORIZED_USER);
        boolean isNotAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null;
        if(isNotAuthenticated){
            UserDetails loggedInUser = userDetailServiceImp.loadUserByUsername(decodedToken.username());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loggedInUser,null,loggedInUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,response);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String reqRoutes = request.getServletPath();
        return unProtectedRoutes.stream().anyMatch(e->pathMatcher.match(e,reqRoutes));
    }

    private void exceptionHandling(HttpServletRequest request,HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if(exception instanceof TokenExceptions){
            ExceptionApiResponse<String> customResp = new ExceptionApiResponse<>(exception.getMessage(),null, LocalDateTime.now());
            mapper.writeValue(response.getWriter(),customResp);
        }else {
            ExceptionApiResponse<String> customResp = new ExceptionApiResponse<>(MessageConstant.UNAUTHORIZED_USER,null, LocalDateTime.now());
            mapper.writeValue(response.getWriter(),customResp);
        }

    }
}
