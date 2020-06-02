package com.project.munchkin.users

import com.project.munchkin.base.security.JwtTokenProvider
import com.project.munchkin.users.domain.UserFacade
import com.project.munchkin.users.domain.UserFacadeCreator
import com.project.munchkin.users.dto.SignUpRequest
import com.project.munchkin.users.model.User
import com.project.munchkin.users.repository.UserRepository
import groovy.com.project.munchkin.users.UserInMemoryRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserSpec extends Specification {
    UserRepository userRepository = new UserInMemoryRepository()
    AuthenticationManager authenticationManager = Stub(AuthenticationManager.class)
    PasswordEncoder passwordEncoder = Stub(PasswordEncoder.class)
    JwtTokenProvider tokenProvider = Stub(JwtTokenProvider.class)

    UserFacade userFacade = UserFacadeCreator.createUserFacade(userRepository, authenticationManager, passwordEncoder, tokenProvider)

    def "user should be able to register"() {
        when: "user is registering"
            User user = userFacade.registerUser(SignUpRequest.builder()
                    .email("jj@gmail.com")
                    .inGameName("Jan Kowalski")
                    .userPassword("Ala123!")
                    .username("jj")
                    .build())
        then: "user is registered"
            userRepository.existsById(user.getId())
    }
}
