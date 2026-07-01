package com.AuthForge.AuthForge.service;


import com.AuthForge.AuthForge.domain.Role;
import com.AuthForge.AuthForge.domain.User;
import com.AuthForge.AuthForge.dto.AuthResponse;
import com.AuthForge.AuthForge.dto.LoginRequest;
import com.AuthForge.AuthForge.dto.RegisterRequest;
import com.AuthForge.AuthForge.exception.EmailAlreadyExistsException;
import com.AuthForge.AuthForge.exception.InvalidCredntialException;
import com.AuthForge.AuthForge.repository.UserRepository;
import com.AuthForge.AuthForge.sercurity.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Transactional
    public AuthResponse register(RegisterRequest request){
            if(userRepository.existsByEmail(request.getEmail())){
                throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user=User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.MEMBER)
                .build();
            user= userRepository.save(user);

            return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request){
            User user=userRepository.findByEmail(request.getEmail())
                    .orElseThrow(InvalidCredntialException::new);

            if(!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
                throw new InvalidCredntialException();
            }

            return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user){
            String accessToken= jwtService.generateAccessToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole()
            );
            String refreshToken= jwtService.generateRefreshToken(
                    user.getId()
            );
            return new AuthResponse(accessToken,refreshToken,user.getEmail(),user.getRole().name());
    }
}
