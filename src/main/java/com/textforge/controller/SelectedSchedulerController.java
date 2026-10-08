package com.textforge.controller;

import com.textforge.model.Task;
import com.textforge.service.SchedulerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler")
public class SelectedSchedulerController {

    private final SchedulerService schedulerService;

    public SelectedSchedulerController(
            SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/selected")
    public Object scheduleSelected(
            @RequestBody ScheduleRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule request cannot be empty."
            );
        }

        if (request.getProcessors() <= 0) {
            throw new IllegalArgumentException(
                    "Processor count must be greater than 0."
            );
        }

        if (request.getTasks() == null ||
                request.getTasks().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select at least one task."
            );
        }

        return schedulerService.scheduleSelectedTasks(
                request.getTasks(),
                request.getProcessors()
        );
    }

    public static class ScheduleRequest {

        private List<Task> tasks;
        private int processors;

        public ScheduleRequest() {
        }

        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        public int getProcessors() {
            return processors;
        }

        public void setProcessors(int processors) {
            this.processors = processors;
        }
    }
}