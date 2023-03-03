package com.example;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PactConsumerContractTestResource implements QuarkusTestResourceLifecycleManager {
  @Override
  public Map<String, String> start() {
    return Map.of(
      "quarkus.rest-client.hello-client.url", "http://localhost:8082",
      "quarkus.rest-client.another-client.url", "http://localhost:8083"
    );
  }

  @Override
  public void stop() {

  }
}
