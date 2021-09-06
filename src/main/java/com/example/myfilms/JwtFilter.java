package com.example.myfilms;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import io.jsonwebtoken.*;

public class JwtFilter extends BasicAuthenticationFilter {

    public static String createToken(String user, String role) {

        return Jwts.builder()
                .setSubject(user)
                .claim("login", user)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, SecretKey.getBytes())
                .compact();
    }

    public JwtFilter(AuthenticationManager manager) {
        super(manager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            SecurityContextHolder.getContext().setAuthentication(null);
        } else {
            UsernamePasswordAuthenticationToken authResult = authenticateByToken(header).orElse(null);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        }

        chain.doFilter(request, response);
    }

    private Optional<UsernamePasswordAuthenticationToken> authenticateByToken(String header) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(SecretKey.getBytes())
                    .parseClaimsJws(header.replace("Bearer ", ""));

            String login = claims.getBody().get("login").toString();
            String role = claims.getBody().get("role").toString();

            Set<SimpleGrantedAuthority> authoritySet = Collections.singleton(new SimpleGrantedAuthority(role));

            return Optional.of(new UsernamePasswordAuthenticationToken(login, null, authoritySet));
        } catch (ExpiredJwtException | SignatureException exception) {
            return Optional.empty();
        }
    }
}
