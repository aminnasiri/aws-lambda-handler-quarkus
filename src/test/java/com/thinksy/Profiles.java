package com.thinksy;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Profiles {

    public static class FruitProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Collections.singletonMap("quarkus.lambda.handler","fruitDetector");
        }

        @Override
        public String getConfigProfile() {
            return QuarkusTestProfile.super.getConfigProfile();
        }

        @Override
        public List<TestResourceEntry> testResources() {
            return QuarkusTestProfile.super.testResources();
        }
    }
}

