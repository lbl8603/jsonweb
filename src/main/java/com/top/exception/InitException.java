package com.top.exception;

/**
 * @author lubeilin
 * @date 2021/1/7
 */
public class InitException extends RuntimeException{
    public InitException() {
    }

    public InitException(String message) {
        super(message);
    }

    public InitException(String message, Throwable cause) {
        super(message, cause);
    }
}
