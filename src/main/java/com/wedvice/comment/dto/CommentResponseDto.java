package com.wedvice.comment.dto;

import com.wedvice.comment.entity.Comment;
import com.wedvice.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CommentResponseDto {

    @Schema(description = "신부 닉네임", example = "신부")
    private String groomNickname;

    @Schema(description = "신부 url", example = "https://image.url")
    private String groomImageUrl;

    @Schema(description = "신랑 닉네임", example = "신랑")
    private String brideNickname;

    @Schema(description = "신랑 url", example = "https://image.url")
    private String brideImageUrl;

    @Schema
    private List<CommentDto> commentList;

    public static CommentResponseDto from(User bride, User groom, List<Comment> commentList) {
        var commentDtos = commentList.stream().map(CommentDto::from).toList();
        return CommentResponseDto.builder()
            .groomImageUrl(groom.getProfileImageUrl())
            .groomNickname(groom.getNickname())
            .brideNickname(bride.getNickname())
            .brideImageUrl(bride.getProfileImageUrl())
            .commentList(commentDtos)
            .build();
    }
}
