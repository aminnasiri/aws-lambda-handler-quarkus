package com.thinksy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class InputObject {

    private String name;

    public String getName() {
        return name;
    }

    public InputObject setName(String name) {
        this.name = name;
        return this;
    }
}
