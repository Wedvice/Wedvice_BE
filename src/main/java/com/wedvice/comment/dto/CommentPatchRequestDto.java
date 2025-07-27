package com.wedvice.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentPatchRequestDto {

    @Schema(description = "수정할 내용", example = "수정할 내용")
    private String content;
    @Schema(description = "comment Id", example = "1")
    private Long commentId;

}
