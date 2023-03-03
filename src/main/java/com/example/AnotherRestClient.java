package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api")
@RegisterRestClient(configKey = "another-client")
@RegisterClientHeaders
public interface AnotherRestClient {
	@GET
	@Path("/hello2")
	@Produces(MediaType.TEXT_PLAIN)
	String hello();
}
