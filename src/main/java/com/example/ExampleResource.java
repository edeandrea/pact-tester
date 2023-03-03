package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.logging.Log;

@Path("/hello")
@Produces(MediaType.TEXT_PLAIN)
public class ExampleResource {
	private final HelloRestClient helloRestClient;
	private final AnotherRestClient anotherRestClient;

	@ConfigProperty(name = "quarkus.rest-client.hello-client.url")
	String helloUrl;

	@ConfigProperty(name = "quarkus.rest-client.another-client.url")
	String anotherHelloUrl;

	public ExampleResource(@RestClient HelloRestClient helloRestClient, @RestClient AnotherRestClient anotherRestClient) {
		this.helloRestClient = helloRestClient;
		this.anotherRestClient = anotherRestClient;
	}

	@GET
	@Path("/single")
	public String singleHello() {
		Log.infof("Single hello from %s", this.helloUrl);
		return this.helloRestClient.hello();
	}

	@GET
	@Path("/another")
	public String anotherHello() {
		Log.infof("Another hello from %s", this.anotherHelloUrl);
		return this.anotherRestClient.hello();
	}

	@GET
	@Path("/both")
	public String multipleHello() {
		Log.infof("Both hello from %s and %s", this.helloUrl, this.anotherHelloUrl);

		return String.format(
			"client 1: %s, client 2: %s",
			this.helloRestClient.hello(),
			this.anotherRestClient.hello()
		);
	}
}