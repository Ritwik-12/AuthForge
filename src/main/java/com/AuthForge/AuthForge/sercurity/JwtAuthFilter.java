package com.AuthForge.AuthForge.sercurity;

import com.AuthForge.AuthForge.domain.User;
import com.AuthForge.AuthForge.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;


    private static final Logger logger= LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader=request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        final String token=authHeader.split("Bearer ")[1];

        try{
             Claims claims=jwtService.parseClaims(token);
             if(!"access".equals(claims.get("type",String.class))){
                 filterChain.doFilter(request,response);

                 return;
             }

             if(SecurityContextHolder.getContext().getAuthentication() ==null){
                 UUID userId=UUID.fromString(claims.getSubject());
                 String role=claims.get("role",String.class);

                 User user=userRepository.findById(userId).orElse(null);
              if(user!=null){
                  UsernamePasswordAuthenticationToken authToken=
                          new UsernamePasswordAuthenticationToken(
                                    user,
                                  null,
                                  List.of(new SimpleGrantedAuthority("ROLE_"+role))
                          );

                  authToken.setDetails(
                          new WebAuthenticationDetailsSource().buildDetails(request)
                  );
                  SecurityContextHolder.getContext().setAuthentication(authToken);
              }

             }
        }catch(JwtException ex){
            logger.warn("JWT validation failed: {}",ex.getMessage());
        }
        filterChain.doFilter(request,response);
    }
}
