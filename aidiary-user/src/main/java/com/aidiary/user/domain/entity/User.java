package com.aidiary.user.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer loginAttemptCnt;

    @Builder
    public User(final String email, final String username, final String password, final Status status, final Integer loginAttemptCnt) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.loginAttemptCnt = loginAttemptCnt;
    }
}
