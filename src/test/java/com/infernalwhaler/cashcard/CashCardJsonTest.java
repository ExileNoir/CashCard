package com.infernalwhaler.cashcard;

import com.infernalwhaler.cashcard.model.CashCard;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sdeseure
 * @project cash card
 * @date 8/05/2025
 */

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;
    @Autowired
    private JacksonTester<CashCard[]> jsonList;
    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45, "Sarah"),
                new CashCard(100L, 1.00, "Sarah"),
                new CashCard(101L, 150.00, "Sarah"));
    }

    /**
     * Serialization is a mechanism of converting the STATE of an OBJECT into a byte STREAM
     * Serialization = OBJECT --> STREAM
     */
    @Test
    void cashCardSerializationTest() throws IOException {
        final CashCard cashCard = cashCards[0];

        assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");

        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id").isEqualTo(99);

        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    /**
     * Deserialization is a mechanism where byte STREAM is used to RECREATE the action OBJECT in memory
     * Deserialization = STREAM --> OBJECT
     */
    @Test
    void cashCardDeserializationTest() throws IOException {
        final String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "Sarah"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new CashCard(99L, 123.45, "Sarah"));
        assertThat(json.parseObject(expected).id()).isEqualTo(99L);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    /**
     * JsonList.write serializes the cashCards variable into JSON,
     * then asserts that 'list.json' should contain the same data as serialized cashCard variable.
     */
    @Test
    public void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    @Test
    public void cashCardListDeserializationTest() throws IOException {
        final String expected = """
                [
                   { "id": 99, "amount": 123.45, "owner": "Sarah" },
                   { "id": 100, "amount": 1.00, "owner": "Sarah" },
                   { "id": 101, "amount": 150.00, "owner": "Sarah" }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }
}
