package com.aidiary.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_histories")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginHistoriesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @Column(name = "ip_address", length = 39)
    private String ipAddress;

    @Column(name = "device", length = 100)
    private String device;

    @Column(name = "last_logged_at")
    private LocalDateTime lastLoggedAt;

    public void updateIpAddressAndDevice(String ipAddress, String device) {
        this.ipAddress = ipAddress;
        this.device = device;
    }

    public void updateLastLoggedAt(LocalDateTime lastLoggedAt) {
        this.lastLoggedAt = lastLoggedAt;
    }
}
