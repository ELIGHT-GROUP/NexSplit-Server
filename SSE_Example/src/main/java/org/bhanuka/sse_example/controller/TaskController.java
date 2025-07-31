package org.bhanuka.sse_example.controller;


import org.bhanuka.sse_example.model.Task;
import org.bhanuka.sse_example.service.TaskService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        return taskService.addTask(task.getDescription());
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.getTasks();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTasks() {
        return taskService.subscribe();
    }
}