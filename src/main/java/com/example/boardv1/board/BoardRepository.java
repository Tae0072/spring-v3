package com.example.boardv1.board;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

/**
 * 하이버 네이트 기술
 */
@RequiredArgsConstructor // final이 붙어있는 모든 필드의 초기화 하는 생성자를 만들어줌
@Repository
public class BoardRepository {
    private final EntityManager em; // final은 반드시 초기화를 해줘야함 - 아니면 영원히 null이라서서

    // DI = 의존성 주입(의존하고 있는게 Ioc에 떠 있어야함)
    // public BoardRepository(EntityManager em) { // 컴퍼지션 코드 (생성자)
    // this.em = em;
    // }
    public Board findById(int id) {
        Board board = em.find(Board.class, id);
        return board;
    }

    public List<Board> findAll() {
        Query query = em.createQuery("select b from Board b", Board.class);
        List<Board> list = query.getResultList();
        return list;
    }

    public void findAllV2() {
        em.createQuery("select b.id, b.title from Board b").getResultList();
    }

    public Board save(Board board) {
        em.persist(board);// 영속화(영구히 저장하다.)
        return board;
    }

    public void delete(Board board) {
        em.remove(board);
    }
}
