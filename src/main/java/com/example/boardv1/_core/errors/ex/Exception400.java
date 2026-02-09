package com.example.boardv1._core.errors.ex;

// 유효성 검사 실패시, 중복복
public class Exception400 extends RuntimeException {

    public Exception400(String message) {
        super(message);
    }

}
