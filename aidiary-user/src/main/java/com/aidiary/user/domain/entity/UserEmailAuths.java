package com.aidiary.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UserEmailAuths {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private Integer code;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    @Builder
    public UserEmailAuths(final String email, final Integer code, final LocalDateTime createdAt, final LocalDateTime confirmedAt) {
        this.email = email;
        this.code = code;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
    }

    public void updateConfirmedAt(final LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
