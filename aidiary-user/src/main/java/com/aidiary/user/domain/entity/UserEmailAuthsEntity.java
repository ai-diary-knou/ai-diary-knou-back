package com.aidiary.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "user_email_auths")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserEmailAuthsEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String code;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

}
