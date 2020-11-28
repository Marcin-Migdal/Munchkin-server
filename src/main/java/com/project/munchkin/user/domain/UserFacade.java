package com.project.munchkin.user.domain;

import com.project.munchkin.base.security.JwtTokenProvider;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.user.dto.UserDto;
import com.project.munchkin.user.dto.UserEditRequest;
import com.project.munchkin.user.dto.UserPasswordRecoveryRequest;
import com.project.munchkin.user.dto.UserResponse;
import com.project.munchkin.user.dto.authRequests.LoginRequest;
import com.project.munchkin.user.dto.authRequests.SignUpRequest;
import com.project.munchkin.user.exception.EmailAlreadyExistsException;
import com.project.munchkin.user.exception.UsernameAlreadyExistsException;
import com.project.munchkin.user.model.User;
import com.project.munchkin.user.repository.UserRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        return tokenProvider.generateToken(authentication);
    }

    public UserResponse registerUser(SignUpRequest signUpRequest) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username " + signUpRequest.getUsername() + " is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + signUpRequest.getUsername() + " already in use!", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .inGameName(signUpRequest.getInGameName())
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .iconUrl("none")
                .gender(signUpRequest.getGender())
                .build();

        user.setUserPassword(passwordEncoder.encode(signUpRequest.getUserPassword()));

        User result = userRepository.save(user);
        return result.response();
    }

    public UserResponse getUserResponse(Long userId) {
        return getUser(userId).response();
    }

    public UserResponse editUser(UserEditRequest userEditRequest, Long userId) {
        UserDto userDto = getUser(userId).dto();

        userDto.setUsername(userEditRequest.getUsername());
        userDto.setInGameName(userEditRequest.getInGameName());
        userDto.setIconUrl(userEditRequest.getIconUrl());
        userDto.setGender(userEditRequest.getGender());

        userRepository.save(User.fromDto(userDto));

        return getUser(userId).response();
    }

    public void changeUserPassword(String newPassword, Long userId) {
        UserDto userDto = getUser(userId).dto();
        userDto.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(User.fromDto(userDto));
    }

    public void passwordRecovery(UserPasswordRecoveryRequest userPasswordRecoveryRequest) {
        if(userRepository.existsByUsername(userPasswordRecoveryRequest.getUsername()) && userRepository.existsByEmail(userPasswordRecoveryRequest.getEmail())){
            User user = userRepository.findByUsernameOrEmail(userPasswordRecoveryRequest.getUsername(), userPasswordRecoveryRequest.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username: "+ userPasswordRecoveryRequest.getUsername() + "or email", userPasswordRecoveryRequest.getEmail(), HttpStatus.NOT_FOUND));

            changeUserPassword(userPasswordRecoveryRequest.getUserPassword(), user.getId());
        }else{
            throw new ResourceNotFoundException("User", "username: "+ userPasswordRecoveryRequest.getUsername() + "or email", userPasswordRecoveryRequest.getEmail(), HttpStatus.NOT_FOUND);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId, HttpStatus.NOT_FOUND));
    }
}
