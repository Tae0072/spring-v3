package com.example.boardv1.reply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ReplyRequest {

    @Data
    public static class ReplySaveDTO {
        @NotNull(message = "사용자를 확인하시오.")
        private int boardId;
        @NotBlank
        @Size(min = 1, message = "댓글을 입력하시오.")
        private String comment;

    }
}
