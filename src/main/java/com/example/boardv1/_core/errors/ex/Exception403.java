package com.example.boardv1._core.errors.ex;

// 권한 없을시
public class Exception403 extends RuntimeException {

    public Exception403(String message) {
        super(message);
    }

}