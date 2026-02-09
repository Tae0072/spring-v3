package com.example.boardv1._core.errors;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.boardv1._core.errors.ex.Exception400;
import com.example.boardv1._core.errors.ex.Exception401;
import com.example.boardv1._core.errors.ex.Exception403;
import com.example.boardv1._core.errors.ex.Exception404;
import com.example.boardv1._core.errors.ex.Exception500;

@RestControllerAdvice // 모든 예외를 처리하는 클래스 (Data응답답)
public class GlobelExceptionHandler {

    // 유효성 검사 실패시, 중복
    @ExceptionHandler(exception = Exception400.class) // 어떤 예외인지 지정하기
    public String ex400(Exception400 e) {
        String html = String.format("""
                <script>
                    alert('%s');
                    history.back();
                </script>
                """, e.getMessage()); // 응답시에 Dara를 응답!
        return html;
    }

    // 인증 실패시
    @ExceptionHandler(exception = Exception401.class) // 어떤 예외인지 지정하기
    public String ex401(Exception401 e) {
        String html = String.format("""
                <script>
                    alert('%s');
                    location.href = '/login-form';
                </script>
                """, e.getMessage()); // 응답시에 Dara를 응답!
        return html;
    }

    // 권한 없을시
    @ExceptionHandler(exception = Exception403.class) // 어떤 예외인지 지정하기
    public String ex403(Exception403 e) {
        String html = String.format("""
                <script>
                    alert('%s');
                    history.back();
                </script>
                """, e.getMessage()); // 응답시에 Dara를 응답!
        // 로그 남기기
        return html;
    }

    // 자원을 찾을 수 없다.(해당 페이지가 없다.)
    @ExceptionHandler(exception = Exception404.class) // 어떤 예외인지 지정하기
    public String ex4(Exception404 e) {
        String html = String.format("""
                <script>
                    alert('%s');
                    history.back();
                </script>
                """, e.getMessage()); // 응답시에 Dara를 응답!
        return html;
    }

    // 서버측 에러
    @ExceptionHandler(exception = Exception500.class) // 어떤 예외인지 지정하기
    public String ex5(Exception500 e) {
        String html = String.format("""
                <script>
                    alert('%s');
                    history.back();
                </script>
                """, "관리자한테 문의하시오."); // 응답시에 Dara를 응답!
        System.out.println("에러" + e.getMessage());
        return html;
    }

}