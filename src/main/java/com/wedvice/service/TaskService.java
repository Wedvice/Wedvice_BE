// TaskService.java
package com.wedvice.service;

import com.wedvice.entity.*;
import com.wedvice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskListRepository taskListRepo;
    private final TaskItemRepository taskItemRepo;
    private final CoupleRepository coupleRepo;

    // 리스트 생성
    @Transactional
    public TaskList createTaskList(Long coupleId, String title) {
        Couple couple = coupleRepo.findById(coupleId).orElseThrow();
        return taskListRepo.save(TaskList.builder()
                .title(title)
                .couple(couple)
                .totalCost(0L)
                .build());
    }

    // 항목 추가
    @Transactional
    public TaskItem addTaskItem(Long taskListId, String content, Long cost) {
        TaskList list = taskListRepo.findById(taskListId).orElseThrow();

        TaskItem item = TaskItem.builder()
                .content(content)
                .cost(cost)
                .isDone(false)
                .taskList(list)
                .build();

        TaskItem saved = taskItemRepo.save(item);
        updateTotalCost(list);
        return saved;
    }

    // 리스트 조회
    public List<TaskList> getTaskLists(Long coupleId) {
        Couple couple = coupleRepo.findById(coupleId).orElseThrow();
        return taskListRepo.findByCouple(couple);
    }

    // 항목 조회
    public List<TaskItem> getTaskItems(Long taskListId) {
        TaskList list = taskListRepo.findById(taskListId).orElseThrow();
        return taskItemRepo.findByTaskList(list);
    }

    // 완료 상태 토글
    @Transactional
    public void toggleDone(Long taskItemId) {
        TaskItem item = taskItemRepo.findById(taskItemId).orElseThrow();
        item.setIsDone(!item.getIsDone());
    }

    // 항목 수정
    @Transactional
    public void updateTaskItem(Long taskItemId, String content, Long cost) {
        TaskItem item = taskItemRepo.findById(taskItemId).orElseThrow();
        item.setContent(content);
        item.setCost(cost);
        updateTotalCost(item.getTaskList());
    }

    // 삭제
    @Transactional
    public void deleteTaskItem(Long taskItemId) {
        TaskItem item = taskItemRepo.findById(taskItemId).orElseThrow();
        TaskList list = item.getTaskList();
        taskItemRepo.delete(item);
        updateTotalCost(list);
    }

    @Transactional
    public void deleteTaskList(Long taskListId) {
        taskListRepo.deleteById(taskListId);
    }

    private void updateTotalCost(TaskList list) {
        List<TaskItem> items = taskItemRepo.findByTaskList(list);
        long total = items.stream().mapToLong(TaskItem::getCost).sum();
        list.setTotalCost(total);
        taskListRepo.save(list);
    }
}
