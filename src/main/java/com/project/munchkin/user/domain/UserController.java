package com.project.munchkin.user.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.user.dto.*;
import com.project.munchkin.user.dto.authRequests.LoginRequest;
import com.project.munchkin.user.dto.authRequests.SignUpRequest;
import com.project.munchkin.user.exception.EmailAlreadyExistsException;
import com.project.munchkin.user.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
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
        try{
            UserResponse userResponse = userFacade.getUserResponse(currentUser.getId());
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
        }
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changeUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, @CurrentUser UserPrincipal currentUser) {
        try{
            userFacade.changeUserPassword(changePasswordRequest.getUserPassword(), currentUser.getId());
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
}