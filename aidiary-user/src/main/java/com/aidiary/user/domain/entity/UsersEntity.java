package com.aidiary.user.domain.entity;

import com.aidiary.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.aidiary.common.enums.UserStatus.*;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UsersEntity extends BaseEntity implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.status.equals(INACTIVE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.status.equals(BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.status.equals(BLOCKED);
    }

    @Override
    public boolean isEnabled() {
        return this.status.equals(ACTIVE);
    }
}
