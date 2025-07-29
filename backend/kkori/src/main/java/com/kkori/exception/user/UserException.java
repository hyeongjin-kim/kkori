package com.kkori.exception.user;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class UserException extends CustomRuntimeException {

    public UserException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static UserException userNotFound(){
        return new UserException(ExceptionCode.USER_NOT_FOUND);
    }
}
