/*package com.project.munchkin

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.logs.users.dto.JwtAuthenticationResponse
import com.project.logs.logs.dto.LogRequest
import com.project.logs.users.dto.LoginRequest
import com.project.logs.users.dto.SignUpRequest
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

    def "Adam should be able to see logs from 2020-04-20"() {
        when: "Adam should be able to register"
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .username("adam_nowak")
                    .userPassword("Ala123")
                    .inGameName("Adam Nowak")
                    .email("jj@gmail.com")
                    .build()
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
        then: "Adam is registered"
            result.andExpect(MockMvcResultMatchers.status().isCreated())
            result.andExpect(MockMvcResultMatchers.content().json("""
            {
                "success":true,
                "message":"User registered successfully"
            }
            """))

        when: "Adam should be able to log in"
            def loginRequest = LoginRequest.builder()
                    .usernameOrEmail("adam_nowak")
                    .userPassword("Ala123")
                    .build()
            result = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
        then: "Adam is logged in"
            result.andExpect(MockMvcResultMatchers.status().isOk())
            result.andExpect(MockMvcResultMatchers.content().json("""
            {
                "tokenType":"Bearer"
            }
            """))

        when: "Adam should be able to add log from 2020-04-20"
            String jwtString = result.andReturn().getResponse().getContentAsString()
            JwtAuthenticationResponse jwt = objectMapper.readValue(jwtString, JwtAuthenticationResponse.class)
            def token = "Bearer " + jwt.accessToken

            LogRequest logRequest = LogRequest.builder()
                    .date("2020-04-20")
                    .activity("Pisanie testów integracyjnych")
                    .activityTime(12345L)
                    .build()

            result = mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/logs")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(logRequest))
                    .header("Authorization", token))

        then: "Log was added"
            result.andExpect(MockMvcResultMatchers.status().isCreated())
            result.andExpect(MockMvcResultMatchers.content().json("""
            {
                "success":true,
                "message":"Log Created Successfully"
            }
            """))

        when: "Adam should be able to see logs from 2020-04-20"
            result = mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/logs/byDate/2020-04-20")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token))
        then: "Adam sees logs from 2020-04-20"
            result.andExpect(MockMvcResultMatchers.status().isOk())
            result.andExpect(MockMvcResultMatchers.content().json("""
            [
                {
                    "id":1,
                    "date":"2020-04-20",
                    "activity":"Pisanie testów integracyjnych",
                    "activityTime":12345
                }
            ]
            """))

        when: "Adam should be able to delete his logs"
            result = mockMvc.perform(MockMvcRequestBuilders
                    .delete("/api/logs/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token))
        then: "Adam's logs are empty"
            result.andExpect(MockMvcResultMatchers.status().isOk())
            result.andExpect(MockMvcResultMatchers.content().string("Log deleted"))
    }
}*/
