package com.project.munchkin.users.domain;

import com.project.munchkin.users.exception.EmailAlreadyExistsException;
import com.project.munchkin.users.exception.UsernameAlreadyExistsException;
import com.project.munchkin.users.model.User;
import com.project.munchkin.users.dto.LoginRequest;
import com.project.munchkin.users.dto.SignUpRequest;
import com.project.munchkin.users.repository.UserRepository;
import com.project.munchkin.base.security.JwtTokenProvider;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class UserFacade {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    public String authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getUserPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return jwt;
    }

    public User registerUser(SignUpRequest signUpRequest) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username " + signUpRequest.getUsername() + " is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + signUpRequest.getUsername() + " already in use!");
        }

        User user = new User(signUpRequest.getInGameName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getUserPassword(), signUpRequest.getIconUrl());

        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        User result = userRepository.save(user);
        return result;
    }

    public User getUser(Long userId) {
        return userRepository.getOne(userId);
    }
}
