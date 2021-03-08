package groovy.com.project.munchkin.users

import com.project.munchkin.base.security.JwtTokenProvider
import com.project.munchkin.user.domain.UserFacade
import com.project.munchkin.user.dto.UserEditRequest
import com.project.munchkin.user.dto.UserPasswordRecoveryRequest
import groovy.com.project.munchkin.utils.MunchkinTestUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserSpec extends Specification {
    AuthenticationManager authenticationManager = Stub(AuthenticationManager)
    JwtTokenProvider tokenProvider = Stub(JwtTokenProvider)
    PasswordEncoder passwordEncoder = Stub(PasswordEncoder){
        encode("Alice4321") >> "encodedAlice4321"
        encode("newPassword") >> "encodedNewPassword"
        encode("PeterPassword") >> "encodedPeterPassword"
        encode("newPeterPassword") >> "encodedNewPeterPassword"
    }

    MunchkinTestUtils munchkinTestUtils = new MunchkinTestUtils( authenticationManager, tokenProvider, passwordEncoder)
    UserFacade userFacade = munchkinTestUtils.getUserFacade()

    def "user should be able to sing up"() {
        when: "user is singing up"
        def userResponse = munchkinTestUtils.registerUser("Morti", "morti1234",
                "morti1234@gmail.com", "morti4321", "male")
        then: "user successfully singed up"
        munchkinTestUtils.userRepository.existsById(userResponse.getId())
    }


    def "we should be able to get userResponse"() {
        given: "user is signed up "
        def signUpUser = munchkinTestUtils.registerUser("Ghost", "Ghost1234",
                "Ghost1234@gmail.com", "Ghost4321", "male")
        when: "User try to get userResponse"
        def userResponse = userFacade.getUserResponse(signUpUser.getId())
        then: "userResponse is returned correctly"
        userResponse
    }

    def "user should be able to edit his profile"() {
        given: "user is signed up "
        def userResponse = munchkinTestUtils.registerUser("John", "John1234",
                "John1234@gmail.com", "John4321", "male")
        when: "user edit his profile"
        def userEditRequest = UserEditRequest.builder()
                .username("femaleJohn1234")
                .inGameName("femaleJohn")
                .gender("female")
                .build()

        userFacade.editUser(userEditRequest, userResponse.getId())
        then: "user is edited successfully"
        def editedUser = userFacade.getUserResponse(userResponse.getId())

        editedUser.getUsername() == userEditRequest.getUsername()&&
                editedUser.getInGameName() == userEditRequest.getInGameName()&&
                editedUser.getGender() == userEditRequest.getGender()
    }

    def "user should be able to change his password"() {
        given: "user is signed up "
        def userResponse = munchkinTestUtils.registerUser("Alice", "Alice1234",
                "Alice1234@gmail.com", "Alice4321", "female")
        when: "user is changing his password"
        def userBeforeChange = userFacade.getUser(userResponse.getId())
        userFacade.changeUserPassword("newPassword", userResponse.getId())
        def userAfterChange = userFacade.getUser(userResponse.getId())
        then: "password is changed successfully"
        userBeforeChange.getUserPassword() != userAfterChange.getUserPassword()
    }

    def "user should be able to recover his password"() {
        given: "user is signed up "
        def userResponse = munchkinTestUtils.registerUser("Peter", "Peter1234",
                "Peter1234@gmail.com", "PeterPassword", "male")
        when: "user is recovering his password"
        def userPasswordRecoveryRequest = UserPasswordRecoveryRequest.builder()
                .username("Peter1234")
                .userPassword("newPeterPassword")
                .email("Peter1234@gmail.com")
                .build()

        def userBeforeChange = userFacade.getUser(userResponse.getId())
        userFacade.passwordRecovery(userPasswordRecoveryRequest)
        def userAfterChange = userFacade.getUser(userResponse.getId())

        then: "password is recovered successfully"
        userBeforeChange.getUserPassword() != userAfterChange.getUserPassword()
    }
}
