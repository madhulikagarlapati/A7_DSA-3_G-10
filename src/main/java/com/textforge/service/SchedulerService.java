package com.textforge.service;

import com.textforge.model.ScheduledTask;
import com.textforge.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SchedulerService {

    private final TaskService taskService;

    public SchedulerService(TaskService taskService) {
        this.taskService = taskService;
    }

    /*
     * Existing scheduler.
     *
     * This continues to support:
     *
     * GET /api/scheduler?processors=4
     */
    public List<ScheduledTask> schedule(
            List<Task> tasks,
            int processorCount) {

        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }

        validateProcessorCount(processorCount);

        taskService.validateDependencies();

        return runScheduler(tasks, processorCount);
    }


    /*
     * New scheduler used by the new UI.
     *
     * It validates the selected task graph independently,
     * so the user can schedule only a selected group of tasks.
     */
    public List<ScheduledTask> scheduleSelectedTasks(
            List<Task> tasks,
            int processorCount) {

        if (tasks == null || tasks.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select at least one task."
            );
        }

        validateProcessorCount(processorCount);

        validateSelectedTasks(tasks);

        return runScheduler(tasks, processorCount);
    }


    private void validateProcessorCount(int processorCount) {

        if (processorCount <= 0) {

            throw new IllegalArgumentException(
                    "Processor count must be greater than 0."
            );
        }
    }


    /*
     * Validates the selected task dependency graph.
     */
    private void validateSelectedTasks(List<Task> tasks) {

        Map<String, Task> taskMap =
                new HashMap<>();

        for (Task task : tasks) {

            if (task == null) {

                throw new IllegalArgumentException(
                        "Selected task cannot be null."
                );
            }

            if (task.getId() == null ||
                    task.getId().trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Every selected task must have an ID."
                );
            }

            String id =
                    task.getId().trim().toLowerCase();

            if (taskMap.containsKey(id)) {

                throw new IllegalArgumentException(
                        "Duplicate selected task ID: "
                                + task.getId()
                );
            }

            if (task.getDuration() <= 0) {

                throw new IllegalArgumentException(
                        "Task " + task.getId()
                                + " must have a duration greater than 0."
                );
            }

            if (task.getPriority() < 1) {

                throw new IllegalArgumentException(
                        "Task " + task.getId()
                                + " must have a priority of at least 1."
                );
            }

            taskMap.put(id, task);
        }


        for (Task task : tasks) {

            if (task.getDependencies() == null) {
                continue;
            }

            Set<String> dependencySet =
                    new HashSet<>();

            for (String dependency :
                    task.getDependencies()) {

                if (dependency == null ||
                        dependency.trim().isEmpty()) {

                    continue;
                }

                String dependencyId =
                        dependency.trim().toLowerCase();

                if (dependencyId.equals(
                        task.getId().trim().toLowerCase())) {

                    throw new IllegalArgumentException(
                            "Task " + task.getId()
                                    + " cannot depend on itself."
                    );
                }

                if (!dependencySet.add(dependencyId)) {

                    throw new IllegalArgumentException(
                            "Task " + task.getId()
                                    + " contains duplicate dependency "
                                    + dependency
                    );
                }

                if (!taskMap.containsKey(dependencyId)) {

                    throw new IllegalArgumentException(
                            "Task " + task.getId()
                                    + " requires "
                                    + dependency
                                    + ". Please include all required dependencies."
                    );
                }
            }
        }


        detectCycles(tasks, taskMap);
    }


    private void detectCycles(
            List<Task> tasks,
            Map<String, Task> taskMap) {

        Set<String> visited =
                new HashSet<>();

        Set<String> recursionStack =
                new HashSet<>();

        for (Task task : tasks) {

            String id =
                    task.getId().trim().toLowerCase();

            if (!visited.contains(id)) {

                detectCycle(
                        task,
                        taskMap,
                        visited,
                        recursionStack
                );
            }
        }
    }


    private void detectCycle(
            Task task,
            Map<String, Task> taskMap,
            Set<String> visited,
            Set<String> recursionStack) {

        String id =
                task.getId().trim().toLowerCase();

        visited.add(id);
        recursionStack.add(id);

        if (task.getDependencies() != null) {

            for (String dependency :
                    task.getDependencies()) {

                if (dependency == null ||
                        dependency.trim().isEmpty()) {

                    continue;
                }

                String dependencyId =
                        dependency.trim().toLowerCase();

                Task dependencyTask =
                        taskMap.get(dependencyId);

                if (dependencyTask == null) {
                    continue;
                }

                if (recursionStack.contains(dependencyId)) {

                    throw new IllegalArgumentException(
                            "Dependency cycle detected involving task "
                                    + dependencyTask.getId()
                    );
                }

                if (!visited.contains(dependencyId)) {

                    detectCycle(
                            dependencyTask,
                            taskMap,
                            visited,
                            recursionStack
                    );
                }
            }
        }

        recursionStack.remove(id);
    }


    /*
     * Core scheduling algorithm.
     *
     * Rules:
     *
     * 1. Dependencies must finish first.
     * 2. Higher priority tasks are selected first.
     * 3. Available processors are reused.
     * 4. Independent tasks can execute in parallel.
     */
    private List<ScheduledTask> runScheduler(
            List<Task> tasks,
            int processorCount) {

        List<ScheduledTask> schedule =
                new ArrayList<>();

        Set<String> completedTasks =
                new HashSet<>();

        Set<String> scheduledTasks =
                new HashSet<>();

        int[] processorAvailableTime =
                new int[processorCount];


        while (completedTasks.size() < tasks.size()) {

            List<Task> readyTasks =
                    new ArrayList<>();


            /*
             * Find tasks whose dependencies have completed.
             */
            for (Task task : tasks) {

                String taskId =
                        task.getId();

                if (scheduledTasks.contains(taskId)) {
                    continue;
                }

                boolean dependenciesCompleted =
                        true;

                if (task.getDependencies() != null) {

                    for (String dependency :
                            task.getDependencies()) {

                        if (!completedTasks.contains(
                                dependency)) {

                            dependenciesCompleted = false;
                            break;
                        }
                    }
                }

                if (dependenciesCompleted) {
                    readyTasks.add(task);
                }
            }


            if (readyTasks.isEmpty()) {

                throw new IllegalArgumentException(
                        "Unable to schedule all tasks. "
                                + "Check the dependency graph."
                );
            }


            /*
             * Higher priority first.
             * If priorities are equal, task ID decides
             * the order to keep the result deterministic.
             */
            readyTasks.sort(
                    Comparator
                            .comparingInt(
                                    Task::getPriority
                            )
                            .reversed()
                            .thenComparing(
                                    Task::getId
                            )
            );


            for (Task task : readyTasks) {

                String taskId =
                        task.getId();

                if (scheduledTasks.contains(taskId)) {
                    continue;
                }


                /*
                 * Find processor that becomes available first.
                 */
                int selectedProcessor = 0;

                for (int i = 1;
                     i < processorCount;
                     i++) {

                    if (processorAvailableTime[i]
                            < processorAvailableTime[
                                    selectedProcessor]) {

                        selectedProcessor = i;
                    }
                }


                /*
                 * Find when all dependencies finish.
                 */
                int dependencyFinishTime = 0;

                if (task.getDependencies() != null) {

                    for (String dependency :
                            task.getDependencies()) {

                        ScheduledTask dependencyTask =
                                findScheduledTask(
                                        schedule,
                                        dependency
                                );

                        if (dependencyTask != null) {

                            dependencyFinishTime =
                                    Math.max(
                                            dependencyFinishTime,
                                            dependencyTask.getEndTime()
                                    );
                        }
                    }
                }


                int startTime =
                        Math.max(
                                processorAvailableTime[
                                        selectedProcessor
                                ],
                                dependencyFinishTime
                        );


                int endTime =
                        startTime + task.getDuration();


                ScheduledTask scheduledTask =
                        new ScheduledTask(
                                task.getId(),
                                task.getName(),
                                task.getPriority(),
                                selectedProcessor + 1,
                                startTime,
                                endTime
                        );


                schedule.add(
                        scheduledTask
                );


                processorAvailableTime[
                        selectedProcessor
                ] = endTime;


                scheduledTasks.add(
                        taskId
                );
            }


            /*
             * Mark tasks complete when their dependencies
             * have completed.
             */
            boolean progress = true;

            while (progress) {

                progress = false;

                for (Task task : tasks) {

                    String taskId =
                            task.getId();

                    if (completedTasks.contains(taskId)) {
                        continue;
                    }

                    ScheduledTask scheduledTask =
                            findScheduledTask(
                                    schedule,
                                    taskId
                            );

                    if (scheduledTask == null) {
                        continue;
                    }

                    boolean dependenciesCompleted =
                            true;

                    if (task.getDependencies() != null) {

                        for (String dependency :
                                task.getDependencies()) {

                            if (!completedTasks.contains(
                                    dependency)) {

                                dependenciesCompleted = false;
                                break;
                            }
                        }
                    }

                    if (dependenciesCompleted) {

                        completedTasks.add(
                                taskId
                        );

                        progress = true;
                    }
                }
            }
        }


        if (scheduledTasks.size() != tasks.size()) {

            throw new IllegalArgumentException(
                    "Unable to schedule all selected tasks."
            );
        }


        schedule.sort(
                Comparator
                        .comparingInt(
                                ScheduledTask::getStartTime
                        )
                        .thenComparingInt(
                                ScheduledTask::getProcessor
                        )
        );


        return schedule;
    }


    private ScheduledTask findScheduledTask(
            List<ScheduledTask> schedule,
            String taskId) {

        for (ScheduledTask task : schedule) {

            if (task.getTaskId().equals(taskId)) {
                return task;
            }
        }

        return null;
    }
}