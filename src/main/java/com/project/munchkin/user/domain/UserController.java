package com.project.munchkin.user.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.user.dto.*;
import com.project.munchkin.user.dto.authRequests.LoginRequest;
import com.project.munchkin.user.dto.authRequests.SignUpRequest;
import com.project.munchkin.user.exception.EmailAlreadyExistsException;
import com.project.munchkin.user.exception.InGameNameAlreadyExistsException;
import com.project.munchkin.user.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserFacade userFacade;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = userFacade.authenticateUser(loginRequest);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            UserResponse userResponse = userFacade.registerUser(signUpRequest);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/auth/user")
                    .buildAndExpand().toUri();
            return ResponseEntity.created(location).body(new ApiResponse <UserResponse>(true, "User registered successfully", userResponse));
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal currentUser){
        return getUser(currentUser.getId());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId){
        try{
            UserResponse userResponse = userFacade.getUserResponse(userId);
            return ResponseEntity.ok().body(new ApiResponse <UserResponse>(true, "User response was found successfully", userResponse));
        }catch ( ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PutMapping("/editUser")
    public ResponseEntity<?> editUser(@Valid @RequestBody UserEditRequest userEditRequest, @CurrentUser UserPrincipal currentUser) {
        try{
            UserResponse userResponse = userFacade.editUser(userEditRequest, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse <UserResponse>(true, "User was edited successfully", userResponse));
        }catch ( ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }catch ( UsernameAlreadyExistsException | InGameNameAlreadyExistsException e){
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changeUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, @CurrentUser UserPrincipal currentUser) {
        try{
            userFacade.editPasswordAuthorization(changePasswordRequest, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse <>(true, "Password was changed successfully"));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/forgottenPassword")
    public ResponseEntity<?> forgottenPassword(@Valid @RequestBody UserPasswordRecoveryRequest userPasswordRecoveryRequest) {
        try{
            userFacade.passwordRecovery(userPasswordRecoveryRequest);
            return ResponseEntity.ok().body(new ApiResponse <>(true, "Password was recovered successfully"));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PostMapping("/editAvatar")
    public ResponseEntity<?> editAvatar(@Valid @RequestParam("image") MultipartFile imageFile, @CurrentUser UserPrincipal currentUser) {
        try{
            userFacade.editAvatar(imageFile, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse <>(true, "file "+ imageFile.getOriginalFilename() + " uploaded successfully"));
        }catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Error occurred while trying to edit avatar"), HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/getAvatar/{userId}")
    public ResponseEntity<?> getAvatar(@PathVariable Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        try {
            byte[] media  = userFacade.getAvatar(userId);
            return new ResponseEntity<>(media, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Error occurred while trying to get avatar"), HttpStatus.NOT_FOUND);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @DeleteMapping("/deleteAvatar")
    public ResponseEntity<?> deleteAvatar( @CurrentUser UserPrincipal currentUser) {
        try {
            userFacade.deleteAvatar(currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse <>(true, "Avatar was deleted successfully"));
        } catch (IOException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Unknown error occurred while deleting avatar"), HttpStatus.BAD_REQUEST);
        }  catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage()), e.getHttpStatus());
        }
    }
}