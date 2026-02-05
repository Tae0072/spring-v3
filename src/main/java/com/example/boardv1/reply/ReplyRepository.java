package com.example.boardv1.reply;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final이 붙어 있는 모든 필드를 초기화하는 생성자를 만들어줌.
@Repository
public class ReplyRepository {

    private final EntityManager em;

    public Reply replySave(Reply reply) {
        em.persist(reply);
        return reply;
    }

    public void delete(Reply reply) {
        em.remove(reply);
    }

    public Optional<Reply> findById(int id) {
        Reply reply = em.find(Reply.class, id);
        return Optional.ofNullable(reply);
    }
}
