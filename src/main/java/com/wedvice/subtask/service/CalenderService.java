package com.wedvice.subtask.service;

import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.subtask.dto.CalenderSummaryResponseDto;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.subtask.repository.SubTaskRepository;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalenderService {

    private final SubTaskRepository subTaskRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CalenderSummaryResponseDto> makeSummary(Long userId, int year, int month) {
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);
        Long coupleId = user.getCouple().getId();
        List<SubTask> subTaskList = subTaskRepository.getSubTasksByDate(coupleId, year, month);
        return subTaskList.stream().map(
            subTask ->
                CalenderSummaryResponseDto.builder().targetDate(subTask.getTargetDate())
                    .id(subTask.getId())
                    .role(String.valueOf(subTask.getRole()))
                    .content(subTask.getContent())
                    .title(subTask.getCoupleTask().getTask().getTitle())
                    .build()
        ).toList();
    }
}
