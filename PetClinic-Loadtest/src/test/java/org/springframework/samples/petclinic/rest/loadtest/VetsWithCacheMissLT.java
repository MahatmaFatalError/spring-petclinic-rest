package org.springframework.samples.petclinic.rest.loadtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.loadtest4j.LoadTester;
import org.loadtest4j.Request;
import org.loadtest4j.Result;
import org.loadtest4j.drivers.gatling.GatlingBuilder;

/**
 *
 * 30 sec call all 500 vets (which is cached) and inject 10 times a cache eviction
 *
 */
@Execution(ExecutionMode.CONCURRENT)
public class VetsWithCacheMissLT {

	private static final LoadTester loadTester = GatlingBuilder.withUrl("http://localhost:9966/petclinic/api").withDuration(Duration.ofSeconds(30)).withUsersPerSecond(2).build();

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void shouldFindVets() {
		List<Request> requests = Arrays.asList(Request.get("/vets/").withHeader("Accept", "application/json"));

		Result result = loadTester.run(requests);

		assertThat(result.getResponseTime().getPercentile(90)).isLessThanOrEqualTo(Duration.ofMillis(1500));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void clearVetsCache() throws InterruptedException, ClientProtocolException, IOException {

		int iterations = 10;
		int pauseMillis = 3000;

		for (int i = 0; i < iterations; i++) {
			Thread.sleep(pauseMillis);
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost("http://localhost:9966/petclinic/api/vets");
			request.addHeader("content-type", "application/json");
			StringEntity params = new StringEntity("{\"firstName\":\"myname\",\"lastName\":\"mylastname\"} ");
			request.setEntity(params);
			HttpResponse response = client.execute(request);
		}
	}

}
