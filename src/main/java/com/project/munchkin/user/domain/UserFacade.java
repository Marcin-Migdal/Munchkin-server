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
import org.springframework.http.ResponseEntity;
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
                signUpRequest.getEmail(), signUpRequest.getUserPassword(), signUpRequest.getIconUrl(), signUpRequest.getGender());;

        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

        User result = userRepository.save(user);
        return result;
    }

    public UserResponse getUserResponse(Long userId) {
        UserDto userDto = getUser(userId).dto();
        return UserResponse.builder()
                .inGameName(userDto.getInGameName())
                .username(userDto.getUsername())
                .iconUrl(userDto.getIconUrl())
                .gender(userDto.getGender())
                .build();
    }

    public ResponseEntity editUser(UserEditRequest userEditRequest, Long userId) {
        UserDto userDto = getUser(userId).dto();

        userDto.setUsername(userEditRequest.getUsername());
        userDto.setInGameName(userEditRequest.getInGameName());
        userDto.setIconUrl(userEditRequest.getIconUrl());
        userDto.setGender(userEditRequest.getGender());

        userRepository.save(User.fromDto(userDto));

        return ResponseEntity.ok("User was edited successfully");
    }

    public ResponseEntity changeUserPassword(String newPassword, Long userId) {
        UserDto userDto = getUser(userId).dto();
        userDto.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(User.fromDto(userDto));

        return ResponseEntity.ok("Password was changed successfully");
    }

    public ResponseEntity passwordRecovery(UserPasswordRecoveryRequest userPasswordRecoveryRequest) {
        if(userRepository.existsByUsername(userPasswordRecoveryRequest.getUsername()) && userRepository.existsByEmail(userPasswordRecoveryRequest.getEmail())){
            User user = userRepository.findByUsernameOrEmail(userPasswordRecoveryRequest.getUsername(), userPasswordRecoveryRequest.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username: "+ userPasswordRecoveryRequest.getUsername() + "or email", userPasswordRecoveryRequest.getEmail()));

            changeUserPassword(userPasswordRecoveryRequest.getUserPassword(), user.getId());

            return ResponseEntity.ok("Password was changed successfully");
        }
        return ResponseEntity.ok("There is no account with email or username like that");
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
    }
}
