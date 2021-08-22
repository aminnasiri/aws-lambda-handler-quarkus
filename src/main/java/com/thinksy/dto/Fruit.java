package com.thinksy.dto;

import com.thinksy.service.AbstractService;
import java.util.Map;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
public class Fruit {

    private String name;
    private Season type;

    public Fruit() {
    }

    public Fruit(String name, Season type) {
        this.name = name;
        this.type = type;
    }

    public static Fruit from(Map<String, AttributeValue> item) {
        Fruit fruit = new Fruit();
        if (item != null && !item.isEmpty()) {
            fruit.setName(item.get(AbstractService.FRUIT_NAME_COL).s());
            var season = Season.valueOf(item.get(AbstractService.FRUIT_TYPE_COL).s());
            fruit.setType(season);
        }
        return fruit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Season getType() {
        return type;
    }

    public void setType(Season type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fruit)) {
            return false;
        }

        Fruit other = (Fruit) obj;

        return Objects.equals(other.name, this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
