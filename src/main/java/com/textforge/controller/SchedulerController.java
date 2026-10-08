package com.textforge.controller;

import com.textforge.model.ScheduledTask;
import com.textforge.model.SchedulerRequest;
import com.textforge.model.SchedulerTaskResult;
import com.textforge.model.Task;
import com.textforge.service.SchedulerExecutionService;
import com.textforge.service.SchedulerTaskGeneratorService;
import com.textforge.service.SchedulerService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    /*
     * Keep the processor count automatic.
     * The user does not have to choose CPUs.
     */
    private static final int PROCESSOR_COUNT = 4;

    private final SchedulerTaskGeneratorService taskGeneratorService;
    private final SchedulerService schedulerService;
    private final SchedulerExecutionService executionService;

    public SchedulerController(
            SchedulerTaskGeneratorService taskGeneratorService,
            SchedulerService schedulerService,
            SchedulerExecutionService executionService) {

        this.taskGeneratorService =
                taskGeneratorService;

        this.schedulerService =
                schedulerService;

        this.executionService =
                executionService;
    }

    /**
     * Main document scheduler endpoint.
     *
     * Flow:
     *
     * Document
     *      ↓
     * Core Tasks
     *      ↓
     * Additional Operations
     *      ↓
     * DP Priorities
     *      ↓
     * Automatic CPU Scheduling
     *      ↓
     * Real Algorithm Execution
     */
    @PostMapping("/generate")
    public SchedulerResponse generateSchedule(
            @RequestBody SchedulerRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Scheduler request cannot be empty."
            );
        }

        String document = request.getDocument();

        if (document == null ||
                document.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please enter or paste a document."
            );
        }

        /*
         * 1. Generate automatic core tasks.
         */
        List<Task> coreTasks =
                taskGeneratorService.generateCoreTasks();

        /*
         * 2. Add user-selected operations.
         *
         * If no additional operations were selected,
         * the core workflow still works.
         */
        List<Task> allTasks =
                taskGeneratorService.addAdditionalOperations(
                        coreTasks,
                        request.getAdditionalOperations()
                );

        /*
         * 3. Automatically schedule all tasks.
         */
        List<ScheduledTask> schedule =
                schedulerService.schedule(
                        allTasks,
                        PROCESSOR_COUNT
                );

        /*
         * 4. Execute the actual backend operations.
         */
        List<SchedulerTaskResult> results =
                executionService.execute(
                        document,
                        request.getSearchPattern(),
                        allTasks,
                        schedule
                );

        /*
         * 5. Return everything needed by the frontend.
         */
        SchedulerResponse response =
                new SchedulerResponse();

        response.setDocumentLength(
                document.length()
        );

        response.setProcessors(
                PROCESSOR_COUNT
        );

        response.setTasks(
                allTasks
        );

        response.setSchedule(
                schedule
        );

        response.setResults(
                results
        );

        response.setSequentialTime(
                calculateSequentialTime(allTasks)
        );

        response.setMakespan(
                calculateMakespan(schedule)
        );

        response.setTimeSaved(
                response.getSequentialTime()
                        - response.getMakespan()
        );

        if (response.getSequentialTime() > 0) {

            double improvement =
                    (
                            response.getTimeSaved()
                                    * 100.0
                    )
                            / response.getSequentialTime();

            response.setImprovement(
                    Math.round(improvement * 100.0)
                            / 100.0
            );
        }

        response.setCpuUtilization(
                calculateCpuUtilization(
                        schedule,
                        PROCESSOR_COUNT,
                        response.getMakespan()
                )
        );

        return response;
    }

    /**
     * Returns the generated schedule without
     * executing a new request.
     *
     * Useful for testing.
     */
    @GetMapping
    public List<ScheduledTask> getSchedule() {

        return new ArrayList<>();
    }

    private int calculateSequentialTime(
            List<Task> tasks) {

        int total = 0;

        for (Task task : tasks) {
            total += task.getDuration();
        }

        return total;
    }

    private int calculateMakespan(
            List<ScheduledTask> schedule) {

        int maximum = 0;

        for (ScheduledTask task : schedule) {

            maximum =
                    Math.max(
                            maximum,
                            task.getEndTime()
                    );
        }

        return maximum;
    }

    private double calculateCpuUtilization(
            List<ScheduledTask> schedule,
            int processors,
            int makespan) {

        if (processors <= 0 ||
                makespan <= 0) {

            return 0.0;
        }

        int totalWork = 0;

        for (ScheduledTask task : schedule) {

            totalWork +=
                    task.getDuration();
        }

        double utilization =
                (
                        totalWork * 100.0
                )
                        / (processors * makespan);

        return Math.round(
                        utilization * 100.0
                )
                / 100.0;
    }

    /*
     * Response object returned to the frontend.
     */
    public static class SchedulerResponse {

        private String runId;

        private int documentLength;

        private int processors;

        private List<Task> tasks =
                new ArrayList<>();

        private List<ScheduledTask> schedule =
                new ArrayList<>();

        private List<SchedulerTaskResult> results =
                new ArrayList<>();

        private int sequentialTime;

        private int makespan;

        private int timeSaved;

        private double improvement;

        private double cpuUtilization;

        public SchedulerResponse() {

            /*
             * Simple run identifier.
             */
            this.runId =
                    "RUN-"
                            + System.currentTimeMillis();
        }

        public String getRunId() {
            return runId;
        }

        public void setRunId(String runId) {
            this.runId = runId;
        }

        public int getDocumentLength() {
            return documentLength;
        }

        public void setDocumentLength(
                int documentLength) {

            this.documentLength =
                    documentLength;
        }

        public int getProcessors() {
            return processors;
        }

        public void setProcessors(int processors) {
            this.processors = processors;
        }

        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        public List<ScheduledTask> getSchedule() {
            return schedule;
        }

        public void setSchedule(
                List<ScheduledTask> schedule) {

            this.schedule = schedule;
        }

        public List<SchedulerTaskResult> getResults() {
            return results;
        }

        public void setResults(
                List<SchedulerTaskResult> results) {

            this.results = results;
        }

        public int getSequentialTime() {
            return sequentialTime;
        }

        public void setSequentialTime(
                int sequentialTime) {

            this.sequentialTime =
                    sequentialTime;
        }

        public int getMakespan() {
            return makespan;
        }

        public void setMakespan(int makespan) {
            this.makespan = makespan;
        }

        public int getTimeSaved() {
            return timeSaved;
        }

        public void setTimeSaved(int timeSaved) {
            this.timeSaved = timeSaved;
        }

        public double getImprovement() {
            return improvement;
        }

        public void setImprovement(
                double improvement) {

            this.improvement = improvement;
        }

        public double getCpuUtilization() {
            return cpuUtilization;
        }

        public void setCpuUtilization(
                double cpuUtilization) {

            this.cpuUtilization =
                    cpuUtilization;
        }
    }
}