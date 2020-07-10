package com.project.munchkin.user.domain;

import com.project.munchkin.base.security.JwtTokenProvider;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.user.dto.EditRequest;
import com.project.munchkin.user.dto.LoginRequest;
import com.project.munchkin.user.dto.SignUpRequest;
import com.project.munchkin.user.dto.UserDto;
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

    public void editUsername(String username, UserPrincipal currentUser) {
        UserDto userDto = getUser(currentUser.getId()).dto();
        userDto.setUsername(username);
        userRepository.save(User.fromDto(userDto));
    }

    public void editInGameName(String inGameName, UserPrincipal currentUser) {
        UserDto userDto = getUser(currentUser.getId()).dto();
        userDto.setInGameName(inGameName);
        userRepository.save(User.fromDto(userDto));
    }

    public void editIcon(String iconUrl, UserPrincipal currentUser) {
        UserDto userDto = getUser(currentUser.getId()).dto();
        userDto.setIconUrl(iconUrl);
        userRepository.save(User.fromDto(userDto));
    }

    public void changeUserPassword(String newPassword, Long userId) {
        UserDto userDto = getUser(userId).dto();
        userDto.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(User.fromDto(userDto));
    }

    public void changeUserGender(UserPrincipal currentUser) {
        UserDto userDto = getUser(currentUser.getId()).dto();
        String newGender = userDto.getGender().equals("men")  ? "women" : "men";
        userDto.setGender(newGender);
        userRepository.save(User.fromDto(userDto));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
    }

    public ResponseEntity<?> passwordRecovery(EditRequest editRequest) {
        if(userRepository.existsByUsername(editRequest.getUsername()) || userRepository.existsByEmail(editRequest.getEmail())){
            User user = userRepository.findByUsernameOrEmail(editRequest.getUsername(), editRequest.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username or email", editRequest.getUsername()));

            changeUserPassword(editRequest.getUserPassword(), user.getId());

            return ResponseEntity.ok("Password was changed successfully");
        }
        return ResponseEntity.ok("There is no account with email or username like that");
    }
}
