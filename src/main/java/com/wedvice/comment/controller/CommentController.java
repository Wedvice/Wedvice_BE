package com.wedvice.comment.controller;


import com.wedvice.comment.dto.CommentDeleteRequestDto;
import com.wedvice.comment.dto.CommentPatchRequestDto;
import com.wedvice.comment.dto.CommentPostRequestDto;
import com.wedvice.comment.dto.CommentResponseDto;
import com.wedvice.comment.exception.CommentNotFoundException;
import com.wedvice.comment.service.CommentService;
import com.wedvice.common.ApiResponse;
import com.wedvice.common.swagger.DocumentedApiError;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.exception.PartnerNotFoundException;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.subtask.exception.SubTaskNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "Comment API", description = "댓글 API (댓글 CRUD)")
@SecurityRequirement(name = "JWT")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{subtaskId}")
    @Operation(summary = "서브테스크 댓글조회",
        description = "서브테스크 id로 모든 댓글을 조회합니다.")
    @DocumentedApiError(InvalidUserAccessException.class)
    @DocumentedApiError(SubTaskNotFoundException.class)
    @DocumentedApiError(NotMatchedYetException.class)
    @DocumentedApiError(PartnerNotFoundException.class)
    public ResponseEntity<ApiResponse<CommentResponseDto>> getAllComment(
        @LoginUser CustomUserDetails loginUser,
        @PathVariable("subtaskId") Long subtaskId) {
        CommentResponseDto commentResponseDto = commentService.getAllComment(
            loginUser.getUserId(), subtaskId);
        return ResponseEntity.ok(ApiResponse.success(commentResponseDto));
    }

    @PostMapping
    @Operation(summary = "댓글 생성",
        description = "서브테스크 id와 content로 댓글을 생성합니다.")
    @DocumentedApiError(InvalidUserAccessException.class)
    @DocumentedApiError(SubTaskNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> postComment(@LoginUser CustomUserDetails loginUser,
        @RequestBody CommentPostRequestDto commentPostRequestDto) {
        commentService.postComment(loginUser.getUserId(), commentPostRequestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @PatchMapping
    @Operation(summary = "댓글 수정",
        description = "comment id와 content로 댓글을 수정합니다.")
    @DocumentedApiError(InvalidUserAccessException.class)
    @DocumentedApiError(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> updateComment(@LoginUser CustomUserDetails loginUser,
        @RequestBody CommentPatchRequestDto commentPostRequestDto) {
        commentService.updateComment(loginUser.getUserId(), commentPostRequestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping
    @Operation(summary = "댓글 삭제",
        description = "comment id로 댓글을 삭제합니다.")
    @DocumentedApiError(InvalidUserAccessException.class)
    @DocumentedApiError(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> deleteComment(@LoginUser CustomUserDetails loginUser,
        @RequestBody CommentDeleteRequestDto commentDeleteRequestDto) {
        commentService.deleteComment(loginUser.getUserId(), commentDeleteRequestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
