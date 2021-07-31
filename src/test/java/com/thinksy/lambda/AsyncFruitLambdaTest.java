package com.thinksy.lambda;

import com.thinksy.Profiles;
import com.thinksy.dto.Fruit;
import com.thinksy.dto.InputObject;
import com.thinksy.service.FruitAsyncService;
import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(Profiles.StreamProfile.class)
class AsyncFruitLambdaTest {

    @InjectMock
    FruitAsyncService fruitAsyncService;


    @Test
    public void testStreamLambdaHandlerSuccess() throws Exception {
        //Make mock object
        when(fruitAsyncService.get(anyString())).thenReturn(
                Uni.createFrom().item(() -> new Fruit("Kiwi Fruit", "This is a new fruit."))
        );

        InputObject inputObject = new InputObject();
        inputObject.setType("Kiwi Fruit");
        inputObject.setName("This is a new fruit.");
        var out  = LambdaClient.invoke(Fruit.class, inputObject);

//        UniAssertSubscriber<Fruit> uniSubscriber = (UniAssertSubscriber<Fruit>) invoke.subscribe().withSubscriber(UniAssertSubscriber.create());
//        uniSubscriber.assertCompleted().assertItem(new Fruit("Kiwi Fruit", "This is a new fruit."));
        //Assertion
        Assertions.assertEquals("Kiwi Fruit", out.getName());
        Assertions.assertEquals("This is a new fruit.", out.getDescription());
    }
}