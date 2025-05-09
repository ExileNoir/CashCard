package com.infernalwhaler.cashcard;

import com.infernalwhaler.cashcard.model.CashCard;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

    /**
     * inject a test helper that’ll allow to make HTTP requests to the locally running application.
     */
    @Autowired
    TestRestTemplate restTemplate;


    /**
     * use restTemplate to make an HTTP GET request to the application endpoint /cashcards/99.
     * DocumentContext converts the response String into a JSON-aware object with lots of helper methods.
     */
    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);
        final Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    /**
     * The origin server SHOULD send a 201 (Created) response
     * Send a 201 (Created) response containing a Location header field that provides an identifier for the primary resource created
     * */
    @Test
    void shouldCreateAnewCashCard() {
        final CashCard cashCard = new CashCard(null, 250.00);
        final ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", cashCard, void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        final URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        final ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        final Number id = documentContext.read("$.id");
        final Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }

}
