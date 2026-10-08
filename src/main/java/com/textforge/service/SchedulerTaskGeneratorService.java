package com.textforge.service;

import com.textforge.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulerTaskGeneratorService {

    private final SchedulerPriorityService priorityService;

    public SchedulerTaskGeneratorService(
            SchedulerPriorityService priorityService) {

        this.priorityService = priorityService;
    }

    /**
     * Generates the automatic core workflow for
     * every document submitted to the scheduler.
     */
    public List<Task> generateCoreTasks() {

        List<Task> tasks = new ArrayList<>();

        /*
         * T1 - Text Cleaning
         */
        tasks.add(createTask(
                "T1",
                "Text Cleaning",
                2,
                List.of()
        ));

        /*
         * T2 - Tokenization
         */
        tasks.add(createTask(
                "T2",
                "Tokenization",
                3,
                List.of("T1")
        ));

        /*
         * T3 - Word Count
         */
        tasks.add(createTask(
                "T3",
                "Word Count",
                2,
                List.of("T2")
        ));

        /*
         * T4 - Sentence Count
         */
        tasks.add(createTask(
                "T4",
                "Sentence Count",
                2,
                List.of("T2")
        ));

        /*
         * T5 - Word Frequency
         */
        tasks.add(createTask(
                "T5",
                "Word Frequency",
                4,
                List.of("T2")
        ));

        /*
         * Generate priorities using Dynamic Programming.
         */
        priorityService.generatePriorities(tasks);

        return tasks;
    }

    /**
     * Adds user-selected operations to the
     * automatically generated core tasks.
     */
    public List<Task> addAdditionalOperations(
            List<Task> coreTasks,
            List<String> operations) {

        List<Task> tasks = new ArrayList<>();

        if (coreTasks != null) {
            tasks.addAll(coreTasks);
        }

        if (operations == null || operations.isEmpty()) {

            priorityService.generatePriorities(tasks);

            return tasks;
        }

        int nextId = tasks.size() + 1;

        for (String operation : operations) {

            if (operation == null ||
                    operation.trim().isEmpty()) {
                continue;
            }

            String normalized =
                    operation.trim()
                            .toUpperCase()
                            .replace("-", "_")
                            .replace(" ", "_");

            Task task = createAdditionalTask(
                    "T" + nextId,
                    normalized,
                    tasks
            );

            tasks.add(task);

            nextId++;
        }

        /*
         * Recalculate priorities after adding
         * the user-selected operations.
         */
        priorityService.generatePriorities(tasks);

        return tasks;
    }

    private Task createTask(
            String id,
            String name,
            int duration,
            List<String> dependencies) {

        Task task = new Task();

        task.setId(id);
        task.setName(name);
        task.setDuration(duration);

        /*
         * Priority is generated automatically
         * by SchedulerPriorityService.
         */
        task.setPriority(1);

        task.setDependencies(
                new ArrayList<>(dependencies)
        );

        task.setSource("Automatic");
        task.setStatus("Waiting");

        return task;
    }

    private Task createAdditionalTask(
            String id,
            String operation,
            List<Task> existingTasks) {

        String name;
        int duration;

        switch (operation) {

            case "NAIVE_SEARCH":
                name = "Naive Search";
                duration = 4;
                break;

            case "KMP":
            case "KMP_SEARCH":
                name = "KMP Search";
                duration = 4;
                break;

            case "Z":
            case "Z_SEARCH":
            case "Z_ALGORITHM":
                name = "Z Algorithm";
                duration = 4;
                break;

            case "RABIN_KARP":
            case "RABIN_KARP_SEARCH":
                name = "Rabin–Karp";
                duration = 4;
                break;

            case "WORD_COUNT":
                name = "Word Count";
                duration = 2;
                break;

            case "CHARACTER_COUNT":
                name = "Character Count";
                duration = 2;
                break;

            case "SENTENCE_COUNT":
                name = "Sentence Count";
                duration = 2;
                break;

            case "WORD_FREQUENCY":
                name = "Word Frequency";
                duration = 4;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported scheduler operation: "
                                + operation
                );
        }

        /*
         * Additional document operations depend on
         * Tokenization because they operate on the
         * processed document workflow.
         */
        List<String> dependencies =
                new ArrayList<>();

        if (!existingTasks.isEmpty()) {

            boolean hasTokenization =
                    existingTasks.stream()
                            .anyMatch(task ->
                                    "Tokenization"
                                            .equals(task.getName()));

            if (hasTokenization) {
                dependencies.add("T2");
            }
        }

        Task task = new Task();

        task.setId(id);
        task.setName(name);
        task.setDuration(duration);
        task.setPriority(1);
        task.setDependencies(dependencies);
        task.setSource("User Selected");
        task.setStatus("Waiting");

        return task;
    }
}