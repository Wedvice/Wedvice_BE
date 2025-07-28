package com.wedvice.comment.dto;

import com.wedvice.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CommentDto {

    @Schema(description = "댓글Id", example = "1")
    private Long commentId;

    @Schema(description = "닉네임", example = "예랑")
    private String nickName;

    @Schema(description = "내용", example = "친구가 추천해준 업체인데 여기 잘하나봐.")
    private String content;


    @Schema(description = "생성된 시간", example = "2025-07-27 06:25:31")
    private LocalDateTime createdTime;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
            .commentId(comment.getCommentId())
            .content(comment.getContent())
            .createdTime(comment.getCreatedAt())
            .nickName(comment.getUserNickname())
            .build();
    }
}
