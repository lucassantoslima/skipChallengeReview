package com.skipthedishes.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.skipthedishes.SkipChallengeApplication;
import com.skipthedishes.conf.H2JpaConfig;
import com.skipthedishes.dto.CousineDto;
import com.skipthedishes.dto.CustomerDto;
import com.skipthedishes.resource.CousineResource;

@RunWith(SpringRunner.class)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = { SkipChallengeApplication.class,	H2JpaConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class CousineIT {

	@Autowired
	private CousineResource resource;

	@LocalServerPort 
	private int port;
	
	@Autowired
    private TestRestTemplate restTemplate;

	@Test
	public void contexLoads() throws Exception {
		assertThat(resource).isNotNull(); 
	}
	
	@Test
	public void searchByName() {
	    HttpEntity<String> entity = new HttpEntity<String>(getHearderWithToken());
	    
	    URI build = UriComponentsBuilder.fromUriString("/cousine/search/{searchText}").build("Sushi");
		
	    ResponseEntity<CustomerDto> responseEntity = this.restTemplate.exchange(build, HttpMethod.GET, entity, CustomerDto.class);
		
	    CustomerDto body = responseEntity.getBody();
	    
	    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	    assertEquals("Sushi", body.getName());
	}
	
	@Test
	public void createCousine() {
	    String requestJson = "{\"name\":\"Brazilian\"}"; 
	    
	    HttpEntity<String> entity = new HttpEntity<String>(requestJson, getHearderWithToken());
		
		ResponseEntity<CousineDto> responseEntity = this.restTemplate.postForEntity("/cousine", entity, CousineDto.class);
		CousineDto client = responseEntity.getBody();

		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertEquals("Brazilian", client.getName());
		assertEquals("http://localhost:"+ port +"/api/v1/cousine/3", responseEntity.getHeaders().getLocation().toString()); 
		
	}

	private HttpHeaders getHearderWithToken() {
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getToken()); 
	    headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
	
	@Test
	public void updateCousine() {
	    String requestJson = "{\"name\":\"Brazilian\"}";
	    
	    URI build = UriComponentsBuilder.fromUriString("/cousine/{id}").build(2); 
	    
	    HttpEntity<String> entity = new HttpEntity<String>(requestJson, getHearderWithToken());
		
		ResponseEntity<CousineDto> responseEntity = this.restTemplate.exchange(build, HttpMethod.PUT, entity, CousineDto.class);
		CousineDto client = responseEntity.getBody();

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("Brazilian", client.getName());
	}
	
	@Test
	public void updatePartialCousine() {
	    String requestJson = "{\"name\":\"Brazilian\"}"; 
	    
	    URI build = UriComponentsBuilder.fromUriString("/cousine/{id}").build(2); 
	    
	    HttpEntity<String> entity = new HttpEntity<String>(requestJson,getHearderWithToken());
		
		ResponseEntity<CousineDto> responseEntity = this.restTemplate.exchange(build, HttpMethod.PATCH, entity, CousineDto.class);
		CousineDto client = responseEntity.getBody();

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("Brazilian", client.getName());
	}
	
	@Before
	public void before() {
		
	}

	private String getToken() {
		HttpEntity<CustomerDto> request = new HttpEntity<>(new CustomerDto("felipe@hotmail.com", "Felipe Lima", "Rua Tenente Lira", LocalDateTime.now(), "1234568")); 
		return this.restTemplate.postForObject("http://localhost:" + port + "/api/v1/customer", request, String.class);
	}

}
