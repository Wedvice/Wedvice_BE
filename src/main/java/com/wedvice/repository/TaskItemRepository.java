// TaskItemRepository.java
package com.wedvice.repository;

import com.wedvice.entity.TaskItem;
import com.wedvice.entity.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByTaskList(TaskList taskList);
}
