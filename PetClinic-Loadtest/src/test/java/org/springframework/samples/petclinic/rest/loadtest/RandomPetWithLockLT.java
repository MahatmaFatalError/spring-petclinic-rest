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
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.loadtest4j.LoadTester;
import org.loadtest4j.Request;
import org.loadtest4j.Result;
import org.loadtest4j.drivers.gatling.GatlingBuilder;

/**
 *
 * 30 sec call petById and inject 10 times a table lock
 *
 */
@Execution(ExecutionMode.CONCURRENT)
public class RandomPetWithLockLT {

	private static final LoadTester loadTester = GatlingBuilder.withUrl("http://localhost:9966/petclinic/api")
            .withDuration(Duration.ofSeconds(60))
            .withUsersPerSecond(5)
            .build();


    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void shouldFindPets() {
        List<Request> requests = Arrays.asList(Request.get("/pets/"+ new Random().nextInt(13))
                                                .withHeader("Accept", "application/json"));

        Result result = loadTester.run(requests);

        assertThat(result.getResponseTime().getPercentile(90))
            .isLessThanOrEqualTo(Duration.ofMillis(1500));
    }


    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void lockPets() throws InterruptedException, ClientProtocolException, IOException {

    	int iterations = 10;
    	int pauseMillis = 9000;
    	int lockDuration = 500;

    	for (int i = 0; i < iterations; i++) {
    		long pause =  Math.round(pauseMillis * Math.random());

			Thread.sleep(pause);
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet("http://localhost:9966/petclinic/api/pets/lock?duration=" + lockDuration); //new URIBuilder().setParameter("duration", "1500").build()
			HttpResponse response = client.execute(request);
		}
    }

}
