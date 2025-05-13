// TaskController.java
package com.wedvice.controller;

import com.wedvice.entity.TaskItem;
import com.wedvice.entity.TaskList;
import com.wedvice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/list")
    public ResponseEntity<TaskList> createList(@RequestParam Long coupleId, @RequestParam String title) {
        return ResponseEntity.ok(taskService.createTaskList(coupleId, title));
    }

    @GetMapping("/list/{coupleId}")
    public ResponseEntity<List<TaskList>> getLists(@PathVariable Long coupleId) {
        return ResponseEntity.ok(taskService.getTaskLists(coupleId));
    }

    @DeleteMapping("/list/{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable Long listId) {
        taskService.deleteTaskList(listId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/item")
    public ResponseEntity<TaskItem> addItem(@RequestParam Long taskListId, @RequestParam String content, @RequestParam Long cost) {
        return ResponseEntity.ok(taskService.addTaskItem(taskListId, content, cost));
    }

    @GetMapping("/item/{taskListId}")
    public ResponseEntity<List<TaskItem>> getItems(@PathVariable Long taskListId) {
        return ResponseEntity.ok(taskService.getTaskItems(taskListId));
    }

    @PatchMapping("/item/{itemId}/toggle")
    public ResponseEntity<Void> toggleDone(@PathVariable Long itemId) {
        taskService.toggleDone(itemId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<Void> updateItem(@PathVariable Long itemId, @RequestParam String content, @RequestParam Long cost) {
        taskService.updateTaskItem(itemId, content, cost);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        taskService.deleteTaskItem(itemId);
        return ResponseEntity.ok().build();
    }
}
