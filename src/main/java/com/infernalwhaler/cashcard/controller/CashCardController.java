package com.infernalwhaler.cashcard.controller;

import com.infernalwhaler.cashcard.model.CashCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sdeseure
 * @project cash card
 * @date 9/05/2025
 */

@RestController
@RequestMapping("/cashcards")
public class CashCardController {


    /**
     * @GetMapping marks the method as a handler method.
     * GET requests that match cashcards/{requestedID} will be handled by this method.
     * @PathVariable makes Spring Web aware of the requestedId supplied in the HTTP request.
     * */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        if (requestedId.equals(99L)) {
            final CashCard cashCard = new CashCard(99L, 123.45);
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
