package com.project.munchkin.user.domain;

import com.project.munchkin.base.security.JwtTokenProvider;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.user.dto.*;
import com.project.munchkin.user.dto.authRequests.LoginRequest;
import com.project.munchkin.user.dto.authRequests.SignUpRequest;
import com.project.munchkin.user.exception.EmailAlreadyExistsException;
import com.project.munchkin.user.exception.InGameNameAlreadyExistsException;
import com.project.munchkin.user.exception.UsernameAlreadyExistsException;
import com.project.munchkin.user.model.User;
import com.project.munchkin.user.repository.UserRepository;
import lombok.Builder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public UserResponse registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username " + signUpRequest.getUsername() + " is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByInGameName(signUpRequest.getInGameName())) {
            throw new InGameNameAlreadyExistsException("In game name " + signUpRequest.getInGameName() + "is already in use!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + signUpRequest.getEmail() + "is already in use!", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .inGameName(signUpRequest.getInGameName())
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .gender(signUpRequest.getGender())
                .build();

        user.setUserPassword(passwordEncoder.encode(signUpRequest.getUserPassword()));

        User createdUser = userRepository.save(user);
        return createdUser.response();
    }

    public UserResponse getUserResponse(Long userId) {
        return getUser(userId).response();
    }

    public UserResponse editUser(UserEditRequest userEditRequest, Long userId) {
        UserDto userDto = getUser(userId).dto();

        if(!userDto.getUsername().equals(userEditRequest.getUsername()) && userRepository.existsByUsername(userEditRequest.getUsername())){
            throw new UsernameAlreadyExistsException("Username " + userEditRequest.getUsername() + " is already taken!", HttpStatus.BAD_REQUEST);
        }

        if(!userDto.getInGameName().equals(userEditRequest.getInGameName()) && userRepository.existsByInGameName(userEditRequest.getInGameName())){
            throw new InGameNameAlreadyExistsException("In game name " + userEditRequest.getInGameName() + " already in use!", HttpStatus.BAD_REQUEST);
        }

        userDto.setUsername(userEditRequest.getUsername());
        userDto.setInGameName(userEditRequest.getInGameName());
        userDto.setGender(userEditRequest.getGender());

        userRepository.save(User.fromDto(userDto));

        return getUser(userId).response();
    }

    public void editPasswordAuthorization(ChangePasswordRequest changePasswordRequest , Long userId) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        changePasswordRequest.getUsername(),
                        changePasswordRequest.getOldPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        tokenProvider.generateToken(authentication);
        changeUserPassword(changePasswordRequest.getNewPassword(), userId);
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

    public void editAvatar(MultipartFile imageFile, Long userId) throws IOException {
        UserDto userDto = getUser(userId).dto();

        Path currentPath = Paths.get(".");
        Path absolutePath = currentPath.toAbsolutePath().normalize();
        String folderPath = absolutePath + "\\src\\main\\resources\\static\\photos\\";

        String sha256hex = DigestUtils.sha256Hex(userDto.getInGameName());

        Path path = Paths.get(folderPath + sha256hex + "_avatar");

        byte[] bytes = imageFile.getBytes();
        Files.write(path, bytes);

        userDto.setIconPath(path.toString());
        userRepository.save(User.fromDto(userDto));
    }

    public byte[] getAvatar(Long userId) throws IOException {
        String iconUrl = getUser(userId).dto().getIconPath();
        if(iconUrl == null){
            throw new ResourceNotFoundException("IconUrl", "UserId", userId, HttpStatus.NOT_FOUND);
        }
        InputStream iconStream = new FileInputStream(iconUrl);
        byte[] bytes = IOUtils.toByteArray(iconStream);
        iconStream.close();
        return bytes;
    }

    public void deleteAvatar(Long userId) throws IOException {
        UserDto userDto = getUser(userId).dto();
        if(userDto.getIconPath() != null){
            Files.delete(Paths.get(userDto.getIconPath()));
            userDto.setIconPath(null);
            userRepository.save(User.fromDto(userDto));
            return;
        }
        throw new ResourceNotFoundException("Avatar", "UserId", userId, HttpStatus.BAD_REQUEST);
    }

    private void changeUserPassword(String newPassword, Long userId) {
        UserDto userDto = getUser(userId).dto();
        userDto.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(User.fromDto(userDto));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId, HttpStatus.NOT_FOUND));
    }
}