package com.example.boardv1.board;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.boardv1.reply.Reply;
import com.example.boardv1.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 데이터 베이스 세상의 테이블을 자바 세상에 모델린한 결과 = 엔티티.
 */

@NoArgsConstructor // 디폴트 생성자
@Getter
@Setter // 게터, 세터, 투스트링
@Entity // 해당 어노테이션을 보고, 컴퍼넌트 스캔 후, 데이터베이스 테이블을 생성한다.
@Table(name = "board_tb")
public class Board { // user 1, board n
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    @Lob
    private String content;

    // private Integer userId;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    @OrderBy("id DESC")
    private List<Reply> replies = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createdAt;

    @Override
    public String toString() {
        return "Board [id=" + id + ", title=" + title + ", content=" + content + ", user=" + user + ", createdAt="
                + createdAt + "]";
    }

    // 목록 화면에서 사용할 '태그 없는' 줄거리
    public String getSummary() {
        if (content == null) {
            return "";
        }
        // 1. HTML 태그 제거 (정규표현식)
        String cleanContent = content.replaceAll("<[^>]*>", "");

        // 2. 글자수 제한 (너무 길면 자르기)
        if (cleanContent.length() > 20) {
            return cleanContent.substring(0, 20) + "...";
        }
        return cleanContent;
    }

}
