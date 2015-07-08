package com.redhat.lightblue.camel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    private String id;
    private String name;
    private boolean isProcessed;

    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Event() {}

    public Event(String id, String name, boolean isProcessed) {
        this.id = id;
        this.name = name;
        this.isProcessed = isProcessed;
    }

}
