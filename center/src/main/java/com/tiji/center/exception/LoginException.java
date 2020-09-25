package com.tiji.center.exception;

/**
 * @author 贰拾壹
 * @create 2020-01-08 12:59
 */
public class LoginException extends RuntimeException {
    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }
}

