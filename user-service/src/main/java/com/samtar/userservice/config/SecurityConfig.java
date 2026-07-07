package com.samtar.userservice.config;

import com.samtar.dto.ExceptionApiResponse;
import com.samtar.userservice.constants.MessageConstant;
import com.samtar.userservice.constants.Routes;
import com.samtar.userservice.service.imp.UserDetailServiceImp;
import com.samtar.userservice.service.imp.UserDetailsImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {
    final int passwordStrength;
    final UserDetailsImp userDetailsImp;
    final UserDetailServiceImp userDetailServiceImp;
    final ObjectMapper mapper;

    public SecurityConfig(ObjectMapper mapper, @Value("${password.strength}") int passwordStrength, UserDetailsImp userDetailsImp, UserDetailServiceImp userDetailServiceImp) {
        this.mapper = mapper;
        this.passwordStrength = passwordStrength;
        this.userDetailsImp = userDetailsImp;
        this.userDetailServiceImp = userDetailServiceImp;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg){
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordStrength);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailServiceImp);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityConfig(HttpSecurity httpSecurity) {
        return httpSecurity
                // Disable default session management
                .csrf(AbstractHttpConfigurer::disable)
                // Define public and protected endpoints
                .authorizeHttpRequests(e -> e.requestMatchers(Routes.unprotected)
                        .permitAll()
                        .anyRequest()
                        .authenticated())

                // session management - telling spring security, that we are using stateless
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(e-> e.authenticationEntryPoint((re,res,er)->{
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(res.getWriter(), ExceptionApiResponse.of(null,MessageConstant.UNAUTHORIZED_USER));
                }))
                .build();
    }

}
