package com.wedvice.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentDeleteRequestDto {

    @Schema(description = "삭제할 commentID", example = "1")
    private Long commentId;
}
