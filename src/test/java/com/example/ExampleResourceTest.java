package com.example;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;

import au.com.dius.pact.consumer.dsl.PactDslRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(value = PactConsumerContractTestResource.class, restrictToAnnotatedClass = true)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(pactVersion = PactSpecVersion.V4)
public class ExampleResourceTest {
	private static final String HELLO_URI = "/api/hello";
	private static final String HELLO_RESPONSE = "Hello";

	@InjectSpy
	@RestClient
	HelloRestClient helloRestClient;

	@InjectSpy
	@RestClient
	AnotherRestClient anotherRestClient;

	@Pact(consumer = "pact-tester", provider = "hello-service")
	public V4Pact helloPact(PactDslWithProvider builder) {
		return builder
			.uponReceiving("A hello request")
				.path(HELLO_URI)
				.method(HttpMethod.GET)
				.headers(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
			.willRespondWith()
				.headers(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN))
				.status(Status.OK.getStatusCode())
				.body(PactDslRootValue.stringMatcher(".+", HELLO_RESPONSE))
			.toPact(V4Pact.class);
	}

	@Pact(consumer = "pact-tester", provider = "another-hello-service")
	public V4Pact anotherHelloPact(PactDslWithProvider builder) {
		return builder
			.uponReceiving("Another hello request")
				.path(HELLO_URI + 2)
				.method(HttpMethod.GET)
				.headers(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
			.willRespondWith()
				.headers(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN))
				.status(Status.OK.getStatusCode())
				.body(PactDslRootValue.stringMatcher(".+", HELLO_RESPONSE + 2))
			.toPact(V4Pact.class);
	}

	@Test
	@PactTestFor(pactMethod = "helloPact")
	@MockServerConfig(port = "8082")
	public void singleHello() {
		get("/hello/single").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.TEXT)
			.body(is(HELLO_RESPONSE));
	}

	@Test
	@PactTestFor(pactMethod = "anotherHelloPact")
	@MockServerConfig(port = "8083")
	public void anotherHello() {
		get("/hello/another").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.TEXT)
			.body(is(HELLO_RESPONSE + 2));
	}

	@Test
	@PactTestFor(pactMethod = "helloPact")
	@MockServerConfig(port = "8082")
	public void bothHello1() {
		doReturn(HELLO_RESPONSE + 2)
			.when(this.anotherRestClient)
			.hello();

		get("/hello/both").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.TEXT)
			.body(is(String.format("client 1: %s, client 2: %s", HELLO_RESPONSE, HELLO_RESPONSE + 2)));
	}

	@Test
	@PactTestFor(pactMethod = "anotherHelloPact")
	@MockServerConfig(port = "8083")
	public void bothHello2() {
		doReturn(HELLO_RESPONSE)
			.when(this.helloRestClient)
			.hello();

		get("/hello/both").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.TEXT)
			.body(is(String.format("client 1: %s, client 2: %s", HELLO_RESPONSE, HELLO_RESPONSE + 2)));
	}

//	@Test
//	public void anotherTest() {
//		assertThat("hello")
//			.isEqualTo("hello");
//	}
}