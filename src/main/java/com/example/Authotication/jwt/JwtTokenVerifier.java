package com.example.Authotication.jwt;

import com.example.Authotication.repository.Repository;
import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class JwtTokenVerifier extends OncePerRequestFilter {
    JwtUtils jwtUtils;
    Repository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == "") {
            filterChain.doFilter(request, response);
            return;
        }



        Long user_id = Long.valueOf(jwtUtils.extractAllClaims(token).getSubject());
        try {
        var authorities = repository.userFindById(user_id).getRole();

        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = Collections.singleton(new SimpleGrantedAuthority(authorities.name()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                repository.userFindById(user_id).getUsername(),
                null,
                simpleGrantedAuthorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (
    JwtException e){
        throw new IllegalStateException(String.format("Token %s  cannot be truest",token));
    }

        filterChain.doFilter(request, response);
    }
}
