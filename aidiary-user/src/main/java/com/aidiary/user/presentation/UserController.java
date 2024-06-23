package com.aidiary.user.presentation;

import com.aidiary.common.enums.ErrorCode;
import com.aidiary.common.exception.UserException;
import com.aidiary.common.vo.ResponseBundle.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @GetMapping("/health")
    public ResponseResult health(@RequestParam String test){
        if ("exception".equals(test)) {
            throw new UserException(ErrorCode.INVALID_PARAMETER);
        }
        return ResponseResult.success("Okay Everything is fine");
    }

}
