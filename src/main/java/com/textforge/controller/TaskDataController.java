package com.textforge.controller;

import com.textforge.model.Task;
import com.textforge.service.TaskDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task-data")
public class TaskDataController {

    private final TaskDataService taskDataService;

    public TaskDataController(TaskDataService taskDataService) {
        this.taskDataService = taskDataService;
    }

    @GetMapping("/load")
    public List<Task> loadDefaultTasks() {
        return taskDataService.loadDefaultTasks();
    }
}