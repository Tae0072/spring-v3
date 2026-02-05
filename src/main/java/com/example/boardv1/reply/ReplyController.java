package com.example.boardv1.reply;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/replies/save")
    public String replySave(ReplyRequest.ReplySaveDTO replySaveDTO) throws IOException {
        // 인증(o),권한(x)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null)
            throw new RuntimeException("인증되지 않았습니다요.");

        replyService.댓글쓰기(replySaveDTO.getBoardId(), replySaveDTO.getComment(), sessionUser);
        return "redirect:/boards/" + replySaveDTO.getBoardId();
    }
}
