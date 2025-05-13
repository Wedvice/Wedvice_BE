// TaskListRepository.java
package com.wedvice.repository;

import com.wedvice.entity.Couple;
import com.wedvice.entity.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByCouple(Couple couple);
}
