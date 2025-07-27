package com.wedvice.comment.exception;

import com.wedvice.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends CustomException {

    public CommentNotFoundException() {
        super("해당 댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }
}
