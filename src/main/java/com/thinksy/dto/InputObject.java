package com.thinksy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class InputObject {

    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public InputObject setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public InputObject setType(String type) {
        this.type = type;
        return this;
    }
}
