package com.infernalwhaler.cashcard.controller;

import com.infernalwhaler.cashcard.model.CashCard;
import com.infernalwhaler.cashcard.repository.CashCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author Sdeseure
 * @project cash card
 * @date 9/05/2025
 */

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /**
     * @param requestedId @PathVariable makes Spring Web aware of the requestedId supplied in the HTTP request.
     * @apiNote @GetMapping marks the method as a handler method.
     * GET requests that match cashcards/{requestedID} will be handled by this method.
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        final Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
