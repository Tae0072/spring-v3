package com.example.boardv1.reply;

import com.example.boardv1.user.User;

import lombok.Data;

public class ReplyRequest {

    @Data
    public static class ReplySaveDTO {
        private int boardId;
        private String comment;

    }
}
