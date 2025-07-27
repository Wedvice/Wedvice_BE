package com.wedvice.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentPostRequestDto {

    @Schema(description = "생성할 내용", example = "생성할 내용")
    private String content;
    @Schema(description = "subTask Id", example = "1")
    private Long subTaskId;
}
