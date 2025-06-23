package com.wedvice.subtask.repository;

import com.wedvice.subtask.entity.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubTaskRepository extends JpaRepository<SubTask,Long>, SubTaskCustomRepository {
}
