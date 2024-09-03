package com.aidiary.user.service;

import com.aidiary.common.enums.DuplicateUserValidateType;
import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.utils.SHA256Util;
import com.aidiary.core.entity.UsersEntity;
import com.aidiary.core.service.UserDatabaseReadService;
import com.aidiary.user.model.UserRequestBundle;
import com.aidiary.user.model.UserRequestBundle.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final UserDatabaseReadService userDatabaseReadService;

    public void validateIfUserAlreadyExist(DuplicateUserValidateRequest request) {
        switch (DuplicateUserValidateType.valueOf(request.type().toUpperCase(Locale.ROOT))) {
            case EMAIL -> validateIfEmailAlreadyExist(request.value());
            case NICKNAME -> validateIfNicknameAlreadyExist(request.value());
        }
    }

    public void validateIfEmailAlreadyExist(String email) {
        if (userDatabaseReadService.isUserExistsByEmail(email)) {
            throw new UserException("User already registered. Please use another email.", ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    public void validateIfNicknameAlreadyExist(String nickname) {
        if (userDatabaseReadService.isUserExistsByNickname(nickname)) {
            throw new UserException("User already registered. Please use another nickname.", ErrorCode.USER_ALREADY_REGISTERED);
        }
    }

    public void validateIfUserEmailNotExist(String email) {
        if (!userDatabaseReadService.isUserExistsByEmail(email)) {
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }
    }



    private boolean isPasswordMatching(UserRequestBundle.UserLoginRequest request, UsersEntity usersEntity) throws NoSuchAlgorithmException {
        return usersEntity.getPassword().equals(SHA256Util.getHashString(request.password()));
    }

}
