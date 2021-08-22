package com.thinksy.lambda;

import com.thinksy.Profiles;
import com.thinksy.dto.InputObject;
import com.thinksy.dto.Season;
import com.thinksy.service.FruitService;
import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(Profiles.FruitProfile.class)
public class FruitDetectorLambdaTest {

    @InjectMock
    private FruitService fruitService;

    @Test
    public void testLambdaHandlerSuccess() throws Exception {
        //Make mock object
        when(fruitService.getSeasonOfFruit(anyString())).thenReturn(Season.FALL);

        InputObject in = new InputObject();
        in.setName("Kiwi Fruit");

        Season out = LambdaClient.invoke(Season.class, in);

        //Assertion
        Assertions.assertEquals(Season.FALL.name(), out.name());
    }

}
