package com.textforge.service;

import com.textforge.model.ScheduledTask;
import com.textforge.model.SchedulerTaskResult;
import com.textforge.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class SchedulerExecutionService {

    private final TextProcessingService textProcessingService;
    private final DocumentAnalysisService documentAnalysisService;
    private final SearchService searchService;
    private final AlgorithmService algorithmService;

    public SchedulerExecutionService(
            TextProcessingService textProcessingService,
            DocumentAnalysisService documentAnalysisService,
            SearchService searchService,
            AlgorithmService algorithmService) {

        this.textProcessingService = textProcessingService;
        this.documentAnalysisService = documentAnalysisService;
        this.searchService = searchService;
        this.algorithmService = algorithmService;
    }

    /**
     * Executes the scheduled tasks using the actual
     * TEXTFORGE services.
     */
    public List<SchedulerTaskResult> execute(
            String document,
            String searchPattern,
            List<Task> tasks,
            List<ScheduledTask> schedule) {

        if (document == null) {
            document = "";
        }

        if (searchPattern == null) {
            searchPattern = "";
        }

        Map<String, ScheduledTask> scheduleMap = new HashMap<>();

        for (ScheduledTask scheduledTask : schedule) {
            scheduleMap.put(
                    scheduledTask.getTaskId(),
                    scheduledTask
            );
        }

        List<SchedulerTaskResult> results =
                new ArrayList<>();

        String cleanedText = document;
        List<String> tokens = new ArrayList<>();

        for (Task task : tasks) {

            ScheduledTask scheduledTask =
                    scheduleMap.get(task.getId());

            SchedulerTaskResult result =
                    createResult(task, scheduledTask);

            /*
             * A task is initially waiting.
             */
            result.setStatus("Waiting");

            try {

                /*
                 * Mark task as running before executing
                 * the real backend operation.
                 */
                result.setStatus("Running");

                String operationResult;

                switch (task.getName()) {

                    case "Text Cleaning":

                        cleanedText =
                                textProcessingService
                                        .cleanText(document);

                        operationResult =
                                "Text cleaned successfully";

                        break;

                    case "Tokenization":

                        tokens =
                                textProcessingService
                                        .tokenize(cleanedText);

                        operationResult =
                                "Tokens generated: "
                                        + tokens.size();

                        break;

                    case "Word Count":

                        int wordCount =
                                documentAnalysisService
                                        .countWords(cleanedText);

                        operationResult =
                                "Word count: "
                                        + wordCount;

                        break;

                    case "Character Count":

                        int characterCount =
                                documentAnalysisService
                                        .countCharacters(
                                                cleanedText
                                        );

                        operationResult =
                                "Character count: "
                                        + characterCount;

                        break;

                    case "Sentence Count":

                        int sentenceCount =
                                documentAnalysisService
                                        .countSentences(
                                                cleanedText
                                        );

                        operationResult =
                                "Sentence count: "
                                        + sentenceCount;

                        break;

                    case "Word Frequency":

                        Map<String, Integer> frequency =
                                documentAnalysisService
                                        .wordFrequency(
                                                cleanedText
                                        );

                        operationResult =
                                "Unique words: "
                                        + frequency.size();

                        break;

                    case "Naive Search":

                        validateSearchPattern(
                                searchPattern
                        );

                        int naiveResult =
                                searchService.naiveSearch(
                                        cleanedText,
                                        searchPattern
                                );

                        operationResult =
                                formatSearchResult(
                                        "Naive Search",
                                        naiveResult
                                );

                        break;

                    case "KMP Search":

                        validateSearchPattern(
                                searchPattern
                        );

                        int kmpResult =
                                searchService.kmpSearch(
                                        cleanedText,
                                        searchPattern
                                );

                        operationResult =
                                formatSearchResult(
                                        "KMP Search",
                                        kmpResult
                                );

                        break;

                    case "Z Algorithm":

                        validateSearchPattern(
                                searchPattern
                        );

                        int zResult =
                                searchService.zSearch(
                                        cleanedText,
                                        searchPattern
                                );

                        operationResult =
                                formatSearchResult(
                                        "Z Algorithm",
                                        zResult
                                );

                        break;

                    case "Rabin–Karp":

                    case "Rabin-Karp":

                        validateSearchPattern(
                                searchPattern
                        );

                        int rabinKarpResult =
                                searchService.rabinKarpSearch(
                                        cleanedText,
                                        searchPattern
                                );

                        operationResult =
                                formatSearchResult(
                                        "Rabin-Karp",
                                        rabinKarpResult
                                );

                        break;

                    default:

                        operationResult =
                                "Operation completed";

                        break;
                }

                result.setResult(operationResult);

                /*
                 * The operation really finished.
                 */
                result.setStatus("Completed");

            } catch (Exception exception) {

                result.setStatus("Failed");

                result.setResult(
                        "Execution error: "
                                + exception.getMessage()
                );
            }

            results.add(result);
        }

        return results;
    }

    private SchedulerTaskResult createResult(
            Task task,
            ScheduledTask scheduledTask) {

        SchedulerTaskResult result =
                new SchedulerTaskResult();

        result.setTaskId(task.getId());
        result.setTaskName(task.getName());
        result.setSource(task.getSource());
        result.setPriority(task.getPriority());
        result.setDuration(task.getDuration());

        if (task.getDependencies() != null) {

            result.setDependencies(
                    new ArrayList<>(
                            task.getDependencies()
                    )
            );
        }

        if (scheduledTask != null) {

            result.setProcessor(
                    scheduledTask.getProcessor()
            );

            result.setStartTime(
                    scheduledTask.getStartTime()
            );

            result.setEndTime(
                    scheduledTask.getEndTime()
            );
        }

        return result;
    }

    private void validateSearchPattern(
            String searchPattern) {

        if (searchPattern == null ||
                searchPattern.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "A search pattern is required for search operations."
            );
        }
    }

    private String formatSearchResult(
            String algorithm,
            int index) {

        if (index >= 0) {

            return algorithm
                    + ": pattern found at index "
                    + index;
        }

        return algorithm
                + ": pattern not found";
    }
}