package com.textforge.service;

import com.textforge.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskDataService {

    private final TaskService taskService;

    public TaskDataService(TaskService taskService) {
        this.taskService = taskService;
    }

    public List<Task> loadDefaultTasks() {

        taskService.clearTasks();

        List<Task> tasks = new ArrayList<>();

        tasks.add(new Task(
                "T1",
                "Text Cleaning",
                5,
                3,
                List.of()
        ));

        tasks.add(new Task(
                "T2",
                "Tokenization",
                5,
                2,
                List.of("T1")
        ));

        tasks.add(new Task(
                "T3",
                "Keyword Search",
                4,
                4,
                List.of("T2")
        ));

        tasks.add(new Task(
                "T4",
                "Edit Distance",
                4,
                5,
                List.of("T2")
        ));

        tasks.add(new Task(
                "T5",
                "Document Similarity",
                3,
                6,
                Arrays.asList("T3", "T4")
        ));

        tasks.add(new Task(
                "T6",
                "Frequency Analysis",
                3,
                3,
                List.of("T2")
        ));

        tasks.add(new Task(
                "T7",
                "Pattern Analysis",
                2,
                4,
                Arrays.asList("T5", "T6")
        ));

        tasks.add(new Task(
                "T8",
                "Final Report",
                1,
                2,
                List.of("T7")
        ));

        for (Task task : tasks) {
            taskService.addTask(task);
        }

        return taskService.getAllTasks();
    }
}