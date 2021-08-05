package com.bancolombia.dojo.reactivecommons.messages;

import com.bancolombia.dojo.reactivecommons.model.Task;

public class SaveTask extends Task {
    public static final String NAME = "tasks.save";

    public SaveTask() {
    }

    public SaveTask(String name, String description, String supervisor) {
        super(name, description, supervisor);
    }
}
