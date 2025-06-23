package com.wedvice.task.repository;

import com.wedvice.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>, CustomTaskRepository {

}
