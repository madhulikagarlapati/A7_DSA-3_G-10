package com.textforge.service;

import com.textforge.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task addTask(Task task) {

        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        if (task.getId() == null || task.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID is required.");
        }

        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name is required.");
        }

        if (task.getDuration() <= 0) {
            throw new IllegalArgumentException(
                    "Task duration must be greater than 0."
            );
        }

        if (task.getPriority() < 1) {
            throw new IllegalArgumentException(
                    "Task priority must be at least 1."
            );
        }

        // Check duplicate task ID
        for (Task existing : tasks) {
            if (existing.getId().equalsIgnoreCase(task.getId())) {
                throw new IllegalArgumentException(
                        "Duplicate task ID: " + task.getId()
                );
            }
        }

        // Make sure dependencies are not null
        if (task.getDependencies() == null) {
            task.setDependencies(new ArrayList<>());
        }

        // Check self dependency
        for (String dependency : task.getDependencies()) {

            if (dependency == null || dependency.trim().isEmpty()) {
                continue;
            }

            if (dependency.equalsIgnoreCase(task.getId())) {
                throw new IllegalArgumentException(
                        "Task " + task.getId()
                                + " cannot depend on itself."
                );
            }
        }

        // Check duplicate dependencies
        Set<String> dependencySet = new HashSet<>();

        for (String dependency : task.getDependencies()) {

            if (dependency == null ||
                    dependency.trim().isEmpty()) {
                continue;
            }

            String normalized =
                    dependency.trim().toLowerCase();

            if (!dependencySet.add(normalized)) {
                throw new IllegalArgumentException(
                        "Duplicate dependency '"
                                + dependency
                                + "' in task "
                                + task.getId()
                );
            }
        }

        tasks.add(task);

        // Validate the complete dependency graph
        try {
            validateDependencies();
        } catch (IllegalArgumentException exception) {

            // Remove the task if it made the graph invalid
            tasks.remove(task);

            throw exception;
        }

        return task;
    }

    public void clearTasks() {
        tasks.clear();
    }

    /**
     * Validates the complete task dependency graph.
     */
    public void validateDependencies() {

        // -----------------------------------------
        // 1. Check duplicate IDs
        // -----------------------------------------

        Set<String> ids = new HashSet<>();

        for (Task task : tasks) {

            if (task.getId() == null ||
                    task.getId().trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "A task has an empty ID."
                );
            }

            String normalizedId =
                    task.getId().trim().toLowerCase();

            if (!ids.add(normalizedId)) {

                throw new IllegalArgumentException(
                        "Duplicate task ID: "
                                + task.getId()
                );
            }
        }

        // -----------------------------------------
        // 2. Check dependencies exist
        // -----------------------------------------

        for (Task task : tasks) {

            if (task.getDependencies() == null) {
                continue;
            }

            for (String dependency :
                    task.getDependencies()) {

                if (dependency == null ||
                        dependency.trim().isEmpty()) {

                    continue;
                }

                if (!containsTask(dependency)) {

                    throw new IllegalArgumentException(
                            "Task "
                                    + task.getId()
                                    + " depends on unknown task "
                                    + dependency
                    );
                }
            }
        }

        // -----------------------------------------
        // 3. Check self-dependencies
        // -----------------------------------------

        for (Task task : tasks) {

            if (task.getDependencies() == null) {
                continue;
            }

            for (String dependency :
                    task.getDependencies()) {

                if (dependency.equalsIgnoreCase(
                        task.getId())) {

                    throw new IllegalArgumentException(
                            "Task "
                                    + task.getId()
                                    + " cannot depend on itself."
                    );
                }
            }
        }

        // -----------------------------------------
        // 4. Check duplicate dependencies
        // -----------------------------------------

        for (Task task : tasks) {

            if (task.getDependencies() == null) {
                continue;
            }

            Set<String> dependencySet =
                    new HashSet<>();

            for (String dependency :
                    task.getDependencies()) {

                String normalized =
                        dependency.trim().toLowerCase();

                if (!dependencySet.add(normalized)) {

                    throw new IllegalArgumentException(
                            "Task "
                                    + task.getId()
                                    + " contains duplicate dependency "
                                    + dependency
                    );
                }
            }
        }

        // -----------------------------------------
        // 5. Check dependency cycles
        // -----------------------------------------

        detectCycles();
    }

    private boolean containsTask(String taskId) {

        for (Task task : tasks) {

            if (task.getId().equalsIgnoreCase(taskId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Detect cycles using DFS.
     *
     * WHITE = not visited
     * GRAY  = currently being explored
     * BLACK = completely explored
     */
    private void detectCycles() {

        Set<String> visited =
                new HashSet<>();

        Set<String> recursionStack =
                new HashSet<>();

        for (Task task : tasks) {

            String id =
                    task.getId().toLowerCase();

            if (!visited.contains(id)) {

                detectCycle(
                        task,
                        visited,
                        recursionStack
                );
            }
        }
    }

    private void detectCycle(
            Task task,
            Set<String> visited,
            Set<String> recursionStack) {

        String taskId =
                task.getId().toLowerCase();

        visited.add(taskId);
        recursionStack.add(taskId);

        if (task.getDependencies() != null) {

            for (String dependencyId :
                    task.getDependencies()) {

                Task dependency =
                        findTask(dependencyId);

                if (dependency == null) {
                    continue;
                }

                String dependencyKey =
                        dependency.getId().toLowerCase();

                // Dependency is currently in the
                // recursion stack -> cycle found
                if (recursionStack.contains(
                        dependencyKey)) {

                    throw new IllegalArgumentException(
                            "Dependency cycle detected involving task "
                                    + dependency.getId()
                    );
                }

                // Explore unvisited dependency
                if (!visited.contains(
                        dependencyKey)) {

                    detectCycle(
                            dependency,
                            visited,
                            recursionStack
                    );
                }
            }
        }

        recursionStack.remove(taskId);
    }

    private Task findTask(String taskId) {

        for (Task task : tasks) {

            if (task.getId().equalsIgnoreCase(taskId)) {
                return task;
            }
        }

        return null;
    }
}