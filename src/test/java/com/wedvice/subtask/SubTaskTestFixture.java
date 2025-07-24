package com.wedvice.subtask;

import com.wedvice.couple.entity.Couple;
import com.wedvice.coupletask.entity.CoupleTask;
import com.wedvice.subtask.entity.SubTask;
import com.wedvice.task.entity.Task;
import com.wedvice.user.entity.User.Role;
import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;

public class SubTaskTestFixture {

    public static SubTask createSubTask(Long id, String content, LocalDate date) {
        CoupleTask coupleTask = CoupleTask.create(Task.builder().build(), Couple.create());
        ReflectionTestUtils.setField(coupleTask, "id", id);

        return SubTask.create(
            coupleTask,
            "디스플레이이름",
            1,
            date,
            Role.GROOM,
            10000,
            content
        );
    }
}
