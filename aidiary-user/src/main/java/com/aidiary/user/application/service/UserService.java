package com.aidiary.user.application.service;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    public void validateDuplicateEmail(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorCode.USER_EMAIL_DUPLICATE);
        }
    }
}
