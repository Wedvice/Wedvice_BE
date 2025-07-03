package com.wedvice.task.repository;

import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, CustomTaskRepository {



}
