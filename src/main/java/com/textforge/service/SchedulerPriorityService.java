package com.textforge.service;

import com.textforge.model.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchedulerPriorityService {

    /**
     * Generates task priorities using Dynamic Programming
     * on the task dependency graph.
     *
     * A task receives a higher priority when:
     * - its own duration is large
     * - it lies on a longer dependency path
     * - delaying it can delay more downstream work
     */
    public void generatePriorities(List<Task> tasks) {

        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        Map<String, Task> taskMap = new HashMap<>();

        for (Task task : tasks) {
            taskMap.put(task.getId(), task);
        }

        Map<String, Integer> memo = new HashMap<>();

        int maximumCriticalPath = 1;

        for (Task task : tasks) {

            int score = calculateCriticalPath(
                    task,
                    taskMap,
                    memo
            );

            maximumCriticalPath =
                    Math.max(maximumCriticalPath, score);
        }

        /*
         * Convert the DP critical-path scores
         * into scheduler priorities from 1 to 10.
         */
        for (Task task : tasks) {

            int score = memo.getOrDefault(
                    task.getId(),
                    task.getDuration()
            );

            int priority = (int) Math.ceil(
                    (score * 10.0)
                            / maximumCriticalPath
            );

            priority = Math.max(
                    1,
                    Math.min(10, priority)
            );

            task.setPriority(priority);
        }
    }

    /**
     * Dynamic Programming calculation of the
     * longest dependency path starting from a task.
     */
    private int calculateCriticalPath(
            Task task,
            Map<String, Task> taskMap,
            Map<String, Integer> memo) {

        if (memo.containsKey(task.getId())) {
            return memo.get(task.getId());
        }

        int longestDependentPath = 0;

        for (Task possibleDependent : taskMap.values()) {

            if (possibleDependent
                    .getDependencies()
                    .contains(task.getId())) {

                int dependentScore =
                        calculateCriticalPath(
                                possibleDependent,
                                taskMap,
                                memo
                        );

                longestDependentPath =
                        Math.max(
                                longestDependentPath,
                                dependentScore
                        );
            }
        }

        int score =
                task.getDuration()
                        + longestDependentPath;

        memo.put(task.getId(), score);

        return score;
    }
}