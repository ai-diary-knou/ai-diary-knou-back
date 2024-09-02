package com.aidiary.core.entity;

import com.aidiary.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.aidiary.common.enums.UserStatus.BLOCKED;
import static com.aidiary.common.enums.UserStatus.INACTIVE;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UsersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Integer loginAttemptCnt;

    public void updateLoginAttemptCnt(int loginAttemptCnt){
        this.loginAttemptCnt = loginAttemptCnt;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isAccountNonExpired() { return !this.status.equals(INACTIVE);}

    public boolean isAccountNonLocked() {
        return !this.status.equals(BLOCKED);
    }

    public boolean isSameUser(UsersEntity comparingUser){ return isSameUser(comparingUser.getId()); }

    public boolean isSameUser(Long comparingUserId){ return Objects.equals(this.id, comparingUserId); }

}
