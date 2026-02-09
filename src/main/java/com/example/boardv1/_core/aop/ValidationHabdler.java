package com.example.boardv1._core.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.example.boardv1._core.errors.ex.Exception400;

@Aspect
@Component
public class ValidationHabdler {

    @Before("@annotation(org.springframework.web.bind.annotation.PostMapping)") // 포인트 컷(pointcut) - PostMapping시 발동
    public void validationCheck(JoinPoint jp) {
        // System.out.println("------------------------validationCheck"); PostMapping시
        // 발동하는지 확인.
        for (Object arg : jp.getArgs()) {

            if (arg instanceof Errors errors) {
                if (errors.hasErrors()) {
                    throw new Exception400(errors.getAllErrors().get(0).getDefaultMessage());
                }
            }
            System.out.println("***********" + arg);
        }
    }
}
