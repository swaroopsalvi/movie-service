package com.vinsguru;

import com.vinsguru.domain.Genre;
import com.vinsguru.dto.MovieDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.util.List;

@Import(TestContainersConfiguration.class)
//@SpringBootTest starts the server at the default port 8080
// if you want to start at a random port use the below annotation
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieServiceApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(MovieServiceApplicationTests.class);

	@Autowired
	//TestRestTemplate is a convenient alternative to RestTemplate that is useful in integration tests
	// it is fault tolerant and optionally can carry Basic authentication headers
	// it is configured automatically by Spring Boot it is configured to resolve relative URLs to the running server it is configured to ignore cookies
	private TestRestTemplate template;

	@Test
	public void health(){
		// var is a reserved keyword in Java 10+
		// it infers the type of the variable from the right hand side expression here it infers the type as ResponseEntity<Object>
		// ResponseEntity is a generic class that represents the whole HTTP response including the status code, headers, and body
		// Object is the type of the body
		var responseEntity = this.template.getForEntity("/actuator/health", Object.class);
		Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
	}

	@Test
	void allMovies() {
		var movies = getMovies("/api/movies");
		Assertions.assertEquals(6, movies.size());
	}

	@Test
	void moviesByGenre() {
		var movies = getMovies("/api/movies/ACTION");
		Assertions.assertEquals(3, movies.size());
		Assertions.assertTrue(movies.stream().map(MovieDto::genre).allMatch(Genre.ACTION::equals));
	}

	private List<MovieDto> getMovies(String uri){
		var requestEntity = new RequestEntity<>(HttpMethod.GET, URI.create(uri));
		var responseEntity = this.template.exchange(requestEntity, new ParameterizedTypeReference<List<MovieDto>>() {
		});
		log.info("response: {}", responseEntity.getBody());
		Assertions.assertNotNull(responseEntity.getBody());
		return responseEntity.getBody();
	}

}
