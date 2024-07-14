package com.aidiary.user.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public void updateCodeAndConfirmedAt(String code) {
        this.code = code;
        this.confirmedAt = null;
    }

    public void updateCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void updateConfirmedAt(LocalDateTime confirmedTime) {
        this.confirmedAt = confirmedTime;
    }
}
