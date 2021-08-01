package com.thinksy.service;

import com.thinksy.dto.Fruit;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@ApplicationScoped
public class FruitAsyncService extends AbstractService {

    @Inject
    DynamoDbAsyncClient dynamoDB;

    public Uni<List<Fruit>> findAll() {
        CompletableFuture<ScanResponse> scan = dynamoDB.scan(scanRequest());
        return Uni.createFrom().completionStage(() -> dynamoDB.scan(scanRequest()))
            .onItem()
            .transform(res -> res.items().stream().map(Fruit::from).collect(Collectors.toList()));
    }

    public Uni<List<Fruit>> add(Fruit fruit) {
        return Uni.createFrom().completionStage(() -> dynamoDB.putItem(putRequest(fruit)))
            .onItem().ignore().andSwitchTo(this::findAll);
    }

    public Uni<Fruit> get(String name) {
        return Uni.createFrom().completionStage(() -> dynamoDB.getItem(getRequest(name)))
            .onItem().transform(resp -> Fruit.from(resp.item()));
    }
}