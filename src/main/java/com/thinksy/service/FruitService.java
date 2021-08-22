package com.thinksy.service;

import com.thinksy.dto.Fruit;
import com.thinksy.dto.Season;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FruitService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FruitService.class);

    @Inject
    DynamoDbClient dynamoDB;

    public List<Fruit> findAll() {
        LOGGER.info("Scanning DynamoDB to find out all Fruits");

        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(Fruit::from)
                .collect(Collectors.toList());
    }

    public List<Fruit> add(Fruit fruit) {
        LOGGER.info("Adding a new fruit to DynamoDB.");

        dynamoDB.putItem(putRequest(fruit));
        return findAll();
    }

    public Fruit get(String name) {
        LOGGER.info("Finding out a new fruit from DynamoDB.");

        return Fruit.from(dynamoDB.getItem(getRequest(name)).item());
    }

    public Season getSeasonOfFruit(String name) {
        LOGGER.info("Finding out a type of fruit from DynamoDB.");

        var query = dynamoDB.query(getQuery(name));
        var type = query.items()
                .stream()
                .map(valueMap -> valueMap.get(FRUIT_TYPE_COL).s())
                .findAny().orElseThrow(() -> new RuntimeException("Fruit not found."));

        LOGGER.info("Fruit type is {}", type);

        return Season.valueOf(type.toUpperCase());
    }
}