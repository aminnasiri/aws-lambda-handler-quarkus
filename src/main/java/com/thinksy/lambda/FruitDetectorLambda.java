package com.thinksy.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.thinksy.dto.InputObject;
import com.thinksy.dto.Season;
import com.thinksy.service.FruitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named("fruitDetector")
public class FruitDetectorLambda implements RequestHandler<InputObject, Season> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FruitDetectorLambda.class);

    @Inject
    FruitService service;

    @Override
    public Season handleRequest(InputObject input, Context context) {
        LOGGER.info("Get a request of type of Fruit {}", input.getName());
        return service.getSeasonOfFruit(input.getName());
    }
}
