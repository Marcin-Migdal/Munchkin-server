package com.project.munchkin.user.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.user.dto.EditRequest;
import com.project.munchkin.user.dto.JwtAuthenticationResponse;
import com.project.munchkin.user.dto.LoginRequest;
import com.project.munchkin.user.dto.SignUpRequest;
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

    @GetMapping("/currentUser")
    public Long getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return currentUser.getId();
    }

    @PostMapping("/edit/username")
    public ResponseEntity<?> editUsername(@Valid @RequestBody EditRequest editRequest, @CurrentUser UserPrincipal currentUser) {
        userFacade.editUsername(editRequest.getUsername(), currentUser);
        return ResponseEntity.ok("Username was edited successfully");
    }

    @PostMapping("/edit/inGameName")
    public ResponseEntity<?> editInGameName(@Valid @RequestBody EditRequest editRequest, @CurrentUser UserPrincipal currentUser) {
        userFacade.editInGameName(editRequest.getInGameName(), currentUser);
        return ResponseEntity.ok("In game name was edited successfully");
    }

    @PostMapping("/edit/icon")
    public ResponseEntity<?> editIcon(@Valid @RequestBody EditRequest editRequest, @CurrentUser UserPrincipal currentUser) {
        userFacade.editIcon(editRequest.getIconUrl(), currentUser);
        return ResponseEntity.ok("Icon was edited successfully");
    }

    @PostMapping("/edit/changePassword")
    public ResponseEntity<?> changeUserPassword(@Valid @RequestBody EditRequest editRequest, @CurrentUser UserPrincipal currentUser) {
        userFacade.changeUserPassword(editRequest.getUserPassword(), currentUser.getId());
        return ResponseEntity.ok("Password was changed successfully");
    }

    @PostMapping("/edit/gender")
    public ResponseEntity<?> changeUserGender(@CurrentUser UserPrincipal currentUser) {
        userFacade.changeUserGender(currentUser);
        return ResponseEntity.ok("Gender was changed successfully");
    }

    @PostMapping("/forgottenPassword")
    public ResponseEntity<?> forgottenPassword(@Valid @RequestBody EditRequest editRequest) {
        ResponseEntity<?> responseEntity = userFacade.passwordRecovery(editRequest);
        return responseEntity;
    }
}