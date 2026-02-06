package com.example.boardv1.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.boardv1.reply.Reply;
import com.example.boardv1.user.User;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

// 책임 : 트랜젝션 관리, DTO만들기(일관성을 만들기 위해), 권한체크(DB정보가 필요하니까)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final EntityManager em;

    public List<Board> 게시글목록() {
        return boardRepository.findAll();
    }

    public BoardResponse.DetailDTO 상세보기(int id, Integer sessionUserId) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없어요"));

        return new BoardResponse.DetailDTO(board, sessionUserId);
    }

    @Transactional // update, delete, insert 할때 붙이세요!!
    public void 게시글수정(int id, String title, String content, int sessionUserId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("수정할 게시글을 찾을 수 없어요"));

        if (sessionUserId != board.getUser().getId())
            throw new RuntimeException("수정할 권한이 없습니다.");

        board.setTitle(title);
        board.setContent(content);
    }

    // 원자성(모든게 다되면 commit, 하나라도 실패하면 rollback)
    // 트랜잭션 종료시 flush 됨.
    @Transactional
    public void 게시글쓰기(String title, String content, User sessionUser) {
        // 1. 비영속 객체
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setUser(sessionUser);

        System.out.println("before persist " + board.getId());

        // 2. persist
        boardRepository.save(board);

        System.out.println("after persist " + board.getId());
    }

    @Transactional
    public void 게시글삭제(int id, int sessionUserId) {
        // 영속화
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("삭제할 게시글을 찾을 수 없어요"));

        if (sessionUserId != board.getUser().getId())
            throw new RuntimeException("삭제할 권한이 없습니다.");

        boardRepository.delete(board);
    }

    public Board 수정폼게시글정보(int id, int sessionUserId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없어요"));

        if (sessionUserId != board.getUser().getId())
            throw new RuntimeException("수정정할 권한이 없습니다.");

        return board;
        // 자동 flush
    }

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
}