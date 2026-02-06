package com.example.boardv1.reply;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.boardv1.board.BoardService;
import com.example.boardv1.user.User;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final HttpSession session;

    @PostMapping("/replies/{id}/delete")
    public String delete(@PathVariable("id") int id, @RequestParam("boardId") int boardId) {
        // 인증(o),권한(o)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null)

            throw new RuntimeException("인증되지 않았습니다요.");

        replyService.댓글삭제(id, sessionUser.getId());
        return "redirect:/boards/" + boardId;
    }

    @PostMapping("/api/replies/save") // 1. 주소 확인 (/api 추가)
    public @ResponseBody ResponseEntity<?> save(@RequestBody ReplyRequest.ReplySaveDTO reqDTO, HttpSession session) {

        // 2. 인증 체크 (로그인 안 했으면 401 에러 전송)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED); // 401 보내기
        }

        // 3. 서비스 호출 (리턴받은 reply 객체 사용)
        Reply reply = replyService.댓글쓰기(reqDTO.getBoardId(), reqDTO.getComment(), sessionUser);

        // 4. 응답 DTO 만들기
        ReplyResponse.DTO responseDTO = new ReplyResponse.DTO(reply, sessionUser.getId());

        // 5. 성공 응답 (201 Created 전송)
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
