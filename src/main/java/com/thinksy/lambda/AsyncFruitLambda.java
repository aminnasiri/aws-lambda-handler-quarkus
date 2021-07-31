package com.thinksy.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.thinksy.dto.Fruit;
import com.thinksy.dto.InputObject;
import com.thinksy.service.FruitAsyncService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.atomic.AtomicReference;


@Named("asyncFruit")
public class AsyncFruitLambda implements RequestHandler<InputObject, Fruit> {

    @Inject
    FruitAsyncService service;

    @Override
    public Fruit handleRequest(InputObject input, Context context) {

        LambdaLogger logger = context.getLogger();
        var fruitUni = service.get(input.getName());

        AtomicReference<Fruit> fruitFinal = new AtomicReference<>(new Fruit());
        fruitUni.subscribe().with(
                fruit -> {
                    logger.log("Name:" + fruit.getName() + " Description:" + fruit.getDescription());
                    fruitFinal.set(fruit);

                },
                error -> logger.log("Error" + error)
        );

        return fruitFinal.get();
    }
}
