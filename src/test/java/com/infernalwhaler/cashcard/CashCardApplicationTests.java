package com.infernalwhaler.cashcard;

import com.infernalwhaler.cashcard.model.CashCard;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);
        final Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    /**
     * The origin server SHOULD send a 201 (Created) response
     * Send a 201 (Created) response containing a Location header field that provides an identifier for the primary resource created
     */
    @Test
    @DirtiesContext
    void shouldCreateAnewCashCard() {
        final CashCard newCashCard = new CashCard(null, 250.00, null);
        final ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .postForEntity("/cashcards", newCashCard, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        final URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        final ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity(locationOfNewCashCard, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        final Number id = documentContext.read("$.id");
        final Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }


    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        final JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        final JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards?page=0&size=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnAPageOfCashCardsSortedByAmount() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        final double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.00);
    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        final DocumentContext documentContext = JsonPath.parse(response.getBody());
        final JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        final JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(1.00, 123.45, 150.00);
    }

    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials() {
        final ResponseEntity<String> responseBadUser = restTemplate
                .withBasicAuth("BAD-USER", "abc123")
                .getForEntity("/cashcards/99", String.class);

        assertThat(responseBadUser.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        final ResponseEntity<String> responseBadPwd = restTemplate
                .withBasicAuth("Sarah", "BAD-PASSWORD")
                .getForEntity("/cashcards/99", String.class);

        assertThat(responseBadPwd.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectUserWhoAreNotCardOwners() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("hank-owns-no-cards", "qrs456")
                .getForEntity("/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        final ResponseEntity<String> response = restTemplate
                .withBasicAuth("Sarah", "abc123")
                .getForEntity("/cashcards/102", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
