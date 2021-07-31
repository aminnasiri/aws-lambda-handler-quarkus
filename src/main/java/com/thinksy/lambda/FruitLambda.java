package com.thinksy.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.thinksy.dto.Fruit;
import com.thinksy.dto.InputObject;
import com.thinksy.service.FruitSyncService;
import javax.inject.Inject;
import javax.inject.Named;

@Named("fruit")
public class FruitLambda implements RequestHandler<InputObject, Fruit> {

    @Inject
    FruitSyncService service;

    @Override
    public Fruit handleRequest(InputObject input, Context context) {
        return service.get(input.getName());
    }
}
