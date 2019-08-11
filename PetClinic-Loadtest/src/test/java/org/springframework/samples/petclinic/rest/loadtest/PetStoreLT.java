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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.loadtest4j.LoadTester;
import org.loadtest4j.Request;
import org.loadtest4j.Result;
import org.loadtest4j.drivers.gatling.GatlingBuilder;

@Execution(ExecutionMode.CONCURRENT)
public class PetStoreLT {

	private static final LoadTester loadTester = GatlingBuilder.withUrl("http://localhost:9966/petclinic/api")
            .withDuration(Duration.ofSeconds(5))
            .withUsersPerSecond(1)
            .build();

	private static final LoadTester loadTesterDisruption = GatlingBuilder.withUrl("http://localhost:9966/petclinic/api")
            .withDuration(Duration.ofSeconds(5))
            .withUsersPerSecond(1)
            .build();


    @Test
    @Disabled
    public void shouldFindPetTypes() {
        List<Request> requests = Arrays.asList(Request.get("/pettypes")
                                                .withHeader("Accept", "application/json"));

        Result result = loadTester.run(requests);

        assertThat(result.getResponseTime().getPercentile(90))
            .isLessThanOrEqualTo(Duration.ofMillis(500));
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void shouldFindPets() {
        List<Request> requests = Arrays.asList(Request.get("/pets/"+ new Random().nextInt(50000))
                                                .withHeader("Accept", "application/json"));

        Result result = loadTester.run(requests);

        assertThat(result.getResponseTime().getPercentile(90))
            .isLessThanOrEqualTo(Duration.ofMillis(500));
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void lockPets() throws InterruptedException, ClientProtocolException, IOException {


    	for (int i = 0; i < 3; i++) {
			Thread.sleep(1500);
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet("http://localhost:9966/petclinic/api/pets/lock");
			HttpResponse response = client.execute(request);
		}

//        List<Request> requests = Arrays.asList(Request.get("/pets/lock")
//                                                .withHeader("Accept", "application/json"));
//
//        Result result = loadTesterDisruption.run(requests);

        //assertThat(result.getResponseTime().getPercentile(90)).isLessThanOrEqualTo(Duration.ofMillis(500));
    }

}
