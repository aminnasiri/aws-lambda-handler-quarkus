package com.thinksy.service;

import com.thinksy.dto.Fruit;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public abstract class AbstractService {

    public final static String FRUIT_NAME_COL = "fruitName";
    public final static String FRUIT_TYPE_COL = "fruitType";

    public String getTableName() {
        return "Fruits_TBL";
    }

    protected ScanRequest scanRequest() {

        return ScanRequest.builder().tableName(getTableName())
                .attributesToGet(FRUIT_NAME_COL).build();
    }

    protected PutItemRequest putRequest(Fruit fruit) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(FRUIT_NAME_COL, AttributeValue.builder().s(fruit.getName()).build());
        item.put(FRUIT_TYPE_COL, AttributeValue.builder().s(fruit.getType().name()).build());

        return PutItemRequest.builder()
                .tableName(getTableName())
                .item(item)
                .build();
    }

    protected GetItemRequest getRequest(String name) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(FRUIT_NAME_COL, AttributeValue.builder().s(name).build());

        return GetItemRequest.builder()
                .key(key)
                .tableName(getTableName())
                .build();
    }

    protected QueryRequest getQuery(String name){
        return QueryRequest.builder()
                .tableName(getTableName())
                .keyConditionExpression(FRUIT_NAME_COL + " = :name")
                .expressionAttributeValues(Map.of(":name", AttributeValue.builder().s(name).build()))
                .build();
    }
}