package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.user.application.dto.UserRequestBundle;
import com.aidiary.user.domain.entity.Status;
import com.aidiary.user.domain.entity.User;
import com.aidiary.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void register(final UserRequestBundle.UserRegisterRequest request) {
        if (!request.password().equals(request.rePassword())) {
            throw new UserException(ErrorCode.MISMATCH_PASSWORD);
        }

        User user = userRepository.save(User.builder()
                .username(request.username())
                .email(request.email())
                .status(Status.ACTIVE)
                .password(request.password())
                .build());
    }

    public void validateDuplicateEmail(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    public void validateDuplicateUsername(final String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserException(ErrorCode.USER_ALREADY_REGISTERED);
        }
    }
}
