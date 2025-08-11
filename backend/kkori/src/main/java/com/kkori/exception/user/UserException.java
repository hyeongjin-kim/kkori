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
    
    public static UserException webSocketAuthenticationFailed(){
        return new UserException(ExceptionCode.WEBSOCKET_AUTHENTICATION_FAILED);
    }

    public static UserException unauthorized(){
        return new UserException(ExceptionCode.UNAUTHORIZED_ACCESS);
    }

    public static UserException accessDenied(){
        return new UserException(ExceptionCode.ACCESS_DENIED);
    }

    public static UserException invalidUserFormat(){
        return new UserException(ExceptionCode.INVALID_USER_FORMAT);
    }

    public static UserException adminRequired(){
        return new UserException(ExceptionCode.ADMIN_REQUIRED);
    }
}
