package com.example.boardv1.reply;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.boardv1._core.errors.ex.Exception403;
import com.example.boardv1._core.errors.ex.Exception404;
import com.example.boardv1.board.BoardService;
import com.example.boardv1.user.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final HttpSession session;

    @PostMapping("/api/replies/{id}/delete")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable("id") int id) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED); // 401
        }

        // 2. 삭제 수행 (예외가 발생하면 GlobalHandler로 가지 않게 여기서 잡아야 함)
        try {
            replyService.댓글삭제(id, sessionUser.getId());
            return new ResponseEntity<>(HttpStatus.OK); // 200 성공

        } catch (Exception403 e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN); // 403 권한 없음
        } catch (Exception404 e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 댓글 없음
        }
    }

    @PostMapping("/api/replies/save") // 1. 주소 확인 (/api 추가)
    public ResponseEntity<?> save(@RequestBody @Valid ReplyRequest.ReplySaveDTO reqDTO, Errors errors,
            HttpSession session) {
        // 2. 인증 체크 (로그인 안 했으면 401 에러 전송)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED); // 401 보내기
        }
        // 유효성 검사 실패시
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
            // body에 errorMessage를 담아서 보냄
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        try {
            Reply reply = replyService.댓글쓰기(reqDTO.getBoardId(), reqDTO.getComment(), sessionUser);
            ReplyResponse.DTO responseDTO = new ReplyResponse.DTO(reply, sessionUser.getId());
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED); // 201

        } catch (Exception404 e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        }
    }
}
