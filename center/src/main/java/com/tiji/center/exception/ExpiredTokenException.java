package com.tiji.center.exception;

/**
 * @author 贰拾壹
 * @create 2020-01-08 12:01
 */
public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {
        super();
    }

    public ExpiredTokenException(String message) {
        super(message);
    }
}
