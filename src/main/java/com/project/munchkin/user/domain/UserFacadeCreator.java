package com.project.munchkin.user.domain;

import com.project.munchkin.user.repository.UserRepository;
import com.project.munchkin.base.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserFacadeCreator {

    private UserFacade userFacade;

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider tokenProvider;

    public static UserFacade createUserFacade(UserRepository userRepository, @Autowired AuthenticationManager authenticationManager,
                                              @Autowired PasswordEncoder passwordEncoder, @Autowired JwtTokenProvider tokenProvider) {
        return UserFacade.builder()
                .userRepository(userRepository)
                .authenticationManager(authenticationManager)
                .passwordEncoder(passwordEncoder)
                .tokenProvider(tokenProvider)
                .build();
    }
}
