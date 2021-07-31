package com.thinksy.lambda;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.thinksy.Profiles;
import com.thinksy.dto.Fruit;
import com.thinksy.dto.InputObject;
import com.thinksy.service.FruitSyncService;
import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(Profiles.FruitProfile.class)
public class FruitLambdaTest {

    @InjectMock
    FruitSyncService fruitSyncService;


    @Test
    public void testLambdaHandlerSuccess() throws Exception {
        //Make mock object
        when(fruitSyncService.get(anyString())).thenReturn(
            new Fruit("Kiwi Fruit", "This is a new fruit.")
        );

        InputObject in = new InputObject();
        in.setType("Kiwi Fruit");
        in.setName("This is a new fruit.");

        Fruit out = LambdaClient.invoke(Fruit.class, in);

        //Assertion
        Assertions.assertEquals("Kiwi Fruit", out.getName());
        Assertions.assertEquals("This is a new fruit.", out.getDescription());
    }

}
