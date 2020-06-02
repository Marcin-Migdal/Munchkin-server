package com.project.munchkin.users.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @NotBlank
    String iconUrl;

    public User(String inGameName, String username, String email, String userPassword, String iconUrl) {
        this.inGameName = inGameName;
        this.username = username;
        this.email = email;
        this.userPassword = userPassword;
        this.iconUrl = iconUrl;
    }

    public User(Long id, String inGameName, String username, String email, String userPassword) {
        this.id = id;
        this.inGameName = inGameName;
        this.username = username;
        this.email = email;
        this.userPassword = userPassword;
        this.iconUrl = iconUrl;
    }
}