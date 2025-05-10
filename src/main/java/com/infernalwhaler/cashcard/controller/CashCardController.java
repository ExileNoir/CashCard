package com.infernalwhaler.cashcard.controller;

import com.infernalwhaler.cashcard.model.CashCard;
import com.infernalwhaler.cashcard.repository.CashCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
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
     *                    GET requests that match cashcards/{requestedID} will be handled by this method.
     * @apiNote @GetMapping("/{requestedId}") marks the method as a handler method.
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        final Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        return cashCardOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * @param newCashCard          @RequestBody CashCard newCashCard: the POST expects a request "body" that contains the data submitted to the API
     * @param uriComponentsBuilder uriComponentsBuilder
     * @implNote URI locationOfNewCashCard.
     * Is constructing a URI to the newly created CashCard. This is the URI that the caller can then use to GET the newly created CashCard.
     * @apiNote @PostMapping marks the method as a handler method.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCard, UriComponentsBuilder uriComponentsBuilder) {
        final CashCard savedCashCard = cashCardRepository.save(newCashCard);
        final URI locationOfNewCashCard = uriComponentsBuilder.path("/cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }


    /**
     * @param pageable Since the URI parameters specify page=0&size=1, pageable will contain the values we need.
     * @apiNote PageRequest.of() is a basic Java Bean implementation of Pageable.
     */
    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        final Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }
}
