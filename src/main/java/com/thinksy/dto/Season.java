package com.thinksy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum Season {
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter");

    final String name;

    Season(String seasonName) {
        name = seasonName;
    }
}
