package com.example.boardv1.reply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardv1.board.Board;
import com.example.boardv1.board.BoardRepository;
import com.example.boardv1.user.User;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final EntityManager em;

    @Transactional
    public void 댓글쓰기(int id, String commet, User sessionUser) { // 아이디는 자동 생성
        // 1. 비영속 객체
        Reply reply = new Reply();
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없어요"));

        // User mockUser = em.getReference(User.class, sessionUser); // join 터지기 싫을 때
        // 사용.
        reply.setComment(commet);
        reply.setBoard(board);
        reply.setUser(sessionUser);

        System.out.println("before persist " + reply.getId());

        // 2. persist
        boardRepository.replySave(reply);

        System.out.println("after persist " + reply.getId());
    }

    @Transactional
    public void 댓글삭제(int id, Integer sessionUserId) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없어"));

        if (reply.getUser().getId() != sessionUserId)
            throw new RuntimeException("삭제할 권한이 없습니다.");

        replyRepository.delete(reply);
    }
}
