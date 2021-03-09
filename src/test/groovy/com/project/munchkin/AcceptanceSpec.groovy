package com.project.munchkin

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.munchkin.room.dto.RoomRequest
import com.project.munchkin.user.dto.JwtAuthenticationResponse
import com.project.munchkin.user.dto.authRequests.LoginRequest
import com.project.munchkin.user.dto.authRequests.SignUpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    def "Acceptance tests"() {
        when: "User should be able to register"
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .inGameName("Morti")
                    .username("morti1234")
                    .email("morti1234@gmail.com")
                    .userPassword("mortiPassword1234")
                    .gender("male")
                    .build()
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
        then: "Morti is registered"
            result.andExpect(MockMvcResultMatchers.status().isCreated())
            result.andExpect(MockMvcResultMatchers.content().json("""
            {
                "success":true,
                "message":"User registered successfully"
            }
            """))

        when: "Morti should be able to log in"
            def loginRequest = LoginRequest.builder()
                    .usernameOrEmail("morti1234")
                    .userPassword("mortiPassword1234")
                    .build()
            result = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
        then: "Morti is logged in"
            result.andExpect(MockMvcResultMatchers.status().isOk())
            result.andExpect(MockMvcResultMatchers.content().json("""
                {
                    "tokenType":"Bearer"
                }
                """))

        when: "Morti should be able to add Room"
            String jwtString = result.andReturn().getResponse().getContentAsString()
            JwtAuthenticationResponse jwt = objectMapper.readValue(jwtString, JwtAuthenticationResponse.class)
            def token = "Bearer " + jwt.accessToken

            RoomRequest roomRequest = RoomRequest.builder()
                    .roomName("First test room")
                    .slots(3)
                    .roomPassword("SecretRoomPassword")
                    .build()

            result = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/rooms/addRoom")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roomRequest))
                    .header("Authorization", token))

        then: "Room was added"
            result.andExpect(MockMvcResultMatchers.status().isCreated())
            result.andExpect(MockMvcResultMatchers.content().json("""
                {
                    "success":true,
                    "message":"Room created successfully"
                }
                """))

        when: "Morti should be able to get Rooms"
            result = mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/rooms/getAll/0/1/id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token))

        then: "Rooms were returned"
            result.andExpect(MockMvcResultMatchers.status().isOk())
            result.andExpect(MockMvcResultMatchers.content().json("""
                    {
                        "success":true
                    }
                    """))
    }
}