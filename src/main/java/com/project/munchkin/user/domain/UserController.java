package com.project.munchkin.user.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.user.dto.*;
import com.project.munchkin.user.dto.authRequests.LoginRequest;
import com.project.munchkin.user.dto.authRequests.SignUpRequest;
import com.project.munchkin.user.exception.EmailAlreadyExistsException;
import com.project.munchkin.user.exception.UsernameAlreadyExistsException;
import com.project.munchkin.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            User result = userFacade.registerUser(signUpRequest);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/users/{username}")
                    .buildAndExpand(result.getUsername()).toUri();
            return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        } catch (EmailAlreadyExistsException e) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public UserResponse getCurrentUser(@CurrentUser UserPrincipal currentUser){
        return userFacade.getUserResponse(currentUser.getId());
    }

    @PutMapping("/editUser")
    public ResponseEntity<?> editUser(@Valid @RequestBody UserEditRequest userEditRequest, @CurrentUser UserPrincipal currentUser) {
        return userFacade.editUser(userEditRequest, currentUser.getId());
    }

    @PatchMapping("/edit/changePassword")
    public ResponseEntity<?> changeUserPassword(@Valid @RequestBody UserEditRequest userEditRequest, @CurrentUser UserPrincipal currentUser) {
        return userFacade.changeUserPassword(userEditRequest.getUserPassword(), currentUser.getId());
    }

    @PatchMapping("/forgottenPassword")
    public ResponseEntity<?> forgottenPassword(@Valid @RequestBody UserPasswordRecoveryRequest userPasswordRecoveryRequest) {
        return userFacade.passwordRecovery(userPasswordRecoveryRequest);
    }
}