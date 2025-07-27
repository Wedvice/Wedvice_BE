package com.wedvice.comment.service;

import static com.wedvice.user.entity.User.Role.BRIDE;
import static com.wedvice.user.entity.User.Role.GROOM;

import com.wedvice.comment.dto.CommentPatchRequestDto;
import com.wedvice.comment.dto.CommentPostRequestDto;
import com.wedvice.comment.dto.CommentResponseDto;
import com.wedvice.comment.entity.Comment;
import com.wedvice.comment.repository.CommentRepository;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.exception.SubTaskNotFoundException;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;
    private final SubTaskRepository subTaskRepository;

    @Transactional(readOnly = true)
    public CommentResponseDto getAllComment(Long userId, Long subtaskId) {
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);

        SubTask subTask = subTaskRepository.findById(subtaskId)
            .orElseThrow(SubTaskNotFoundException::new);

        User partner = user.getPartnerOrThrow();
        User bride = user.getRole() == BRIDE ? user : partner;
        User groom = user.getRole() == GROOM ? user : partner;

        List<Comment> commentList = commentRepository.findAllBySubtaskId(subtaskId);

        return CommentResponseDto.from(bride, groom, commentList);
    }

    public void postComment(Long userId, CommentPostRequestDto commentPostRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(InvalidUserAccessException::new);
        SubTask subTask = subTaskRepository.findById(commentPostRequestDto.getSubTaskId())
            .orElseThrow(SubTaskNotFoundException::new);
        Comment comment = Comment.create(user, subTask, commentPostRequestDto.getContent());
        commentRepository.save(comment);
    }

    public void updateComment(Long userId, CommentPatchRequestDto commentPostRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(InvalidUserAccessException::new);

    }
}
