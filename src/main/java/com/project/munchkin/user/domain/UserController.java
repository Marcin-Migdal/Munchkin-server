package com.project.munchkin.user.domain;

import com.project.munchkin.user.exception.EmailAlreadyExistsException;
import com.project.munchkin.user.exception.UsernameAlreadyExistsException;
import com.project.munchkin.user.model.User;
import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.user.dto.JwtAuthenticationResponse;
import com.project.munchkin.user.dto.LoginRequest;
import com.project.munchkin.user.dto.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}