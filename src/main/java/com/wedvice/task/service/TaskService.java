package com.wedvice.task.service;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.coupletask.repository.CoupleTaskRepository;
import com.wedvice.security.login.CustomUserDetails;
import com.wedvice.security.login.LoginUser;
import com.wedvice.task.dto.TaskResponseDTO;
import com.wedvice.task.entity.Task;
import com.wedvice.task.repository.TaskRepository;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import com.wedvice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CoupleTaskRepository coupleTaskRepository;


    public List<Task> findAllTask(){
        return taskRepository.findAll();
    }

    public List<TaskResponseDTO> findAllTaskAndSubTask(CustomUserDetails customUserDetails) {

        Long userId = customUserDetails.getUserId();

        User user = userRepository.findById(userId).orElseThrow();

        Long coupleId = user.getCouple().getId();

        List<CoupleTask> tasks = coupleTaskRepository.findByCoupleIdWithTask(coupleId);

        return tasks.stream()
                .map(ct -> new TaskResponseDTO(
                        ct.getTask().getId(),
                        ct.getTask().getTitle(),
                        (long) ct.getSubTasks().size(),
                        (int) ct.getSubTasks().stream().filter(st -> st.getCompleted()).count()
                ))
                .collect(Collectors.toList());
    }

    public void delete(Long taskId, CustomUserDetails customUserDetails) {


        Long userId = customUserDetails.getUserId();
        User user = userRepository.findById(userId).orElseThrow();
        Long coupleId = user.getCouple().getId();
//        커플task 리포지토리에서 찾고 딜리트로 바꾼다.
        CoupleTask coupleTask = coupleTaskRepository.findByIdAndCoupleId(taskId, coupleId).orElseThrow();

        coupleTask.updateDeleteStatus();

    }
}
