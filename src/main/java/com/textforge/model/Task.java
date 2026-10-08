package com.textforge.model;

import java.util.ArrayList;
import java.util.List;

public class Task {

    private String id;
    private String name;
    private int priority;
    private int duration;
    private List<String> dependencies = new ArrayList<>();

    // New fields for the final scheduler
    private String source;
    private String status;

    public Task() {
    }

    public Task(String id, String name, int priority, int duration,
                List<String> dependencies) {

        this.id = id;
        this.name = name;
        this.priority = priority;
        this.duration = duration;

        if (dependencies != null) {
            this.dependencies = dependencies;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}