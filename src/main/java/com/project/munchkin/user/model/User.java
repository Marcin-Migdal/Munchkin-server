package com.project.munchkin.user.model;

import com.project.munchkin.user.dto.UserDto;
import com.project.munchkin.user.dto.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Size(max = 16)
    String inGameName;

    @NotBlank
    @Size(max = 15)
    String username;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    String email;

    @NotBlank
    @Size(max = 100)
    String userPassword;

    String iconUrl;

    @NotBlank
    String gender;

    public User(Long id, String inGameName, String username, String email, String userPassword, String iconUrl, String gender) {
        this.id = id;
        this.inGameName = inGameName;
        this.username = username;
        this.email = email;
        this.userPassword = userPassword;
        this.iconUrl = iconUrl;
        this.gender = gender;
    }

    public static User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .inGameName(userDto.getInGameName())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .userPassword(userDto.getUserPassword())
                .iconUrl(userDto.getIconUrl())
                .gender(userDto.getGender())
                .build();
    }

    public UserDto dto() {
        return UserDto.builder()
                .id(id)
                .inGameName(inGameName)
                .username(username)
                .email(email)
                .userPassword(userPassword)
                .iconUrl(iconUrl)
                .gender(gender)
                .build();
    }

    public UserResponse response() {
        return UserResponse.builder()
                .id(id)
                .inGameName(inGameName)
                .username(username)
                .gender(gender)
                .build();
    }
}