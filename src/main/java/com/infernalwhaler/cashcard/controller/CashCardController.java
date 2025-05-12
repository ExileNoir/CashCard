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
import java.security.Principal;
import java.util.List;

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
     * @param principal   holds our user's authenticated, authorized information.
     * @apiNote @GetMapping("/{requestedId}") marks the method as a handler method.
     */
    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        final CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());

        if (!cashCardRepository.existsByIdAndOwner(requestedId, principal.getName())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cashCard);
    }

    /**
     * @param pageable  Since the URI parameters specify page=0&size=1, pageable will contain the values we need.
     * @param principal holds our user's authenticated, authorized information.
     * @apiNote PageRequest.of() is a basic Java Bean implementation of Pageable.
     */
    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        final Page<CashCard> page = cashCardRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    /**
     * @param newCashCardRequest   @RequestBody CashCard newCashCardRequest: the POST expects a request "body" that contains the data submitted to the API
     * @param uriComponentsBuilder uriComponentsBuilder
     * @implNote URI locationOfNewCashCard.
     * Is constructing a URI to the newly created CashCard. This is the URI that the caller can then use to GET the newly created CashCard.
     * @apiNote @PostMapping marks the method as a handler method.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder uriComponentsBuilder, Principal principal) {
        final CashCard savedCashCard = cashCardRepository.save(
                new CashCard(null, newCashCardRequest.amount(), principal.getName()));

        final URI locationOfNewCashCard = uriComponentsBuilder
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    /**
     * @param requestedId    @PathVariable makes Spring Web aware of the requestedId supplied in the HTTP request.
     * @param cashCardUpdate @RequestBody CashCard contains the updated CashCard data.
     * @param principal      holds our user's authenticated, authorized information.
     * @return an HTTP 204 NO_CONTENT response code
     * @apiNote @PutMapping("/{requestedId}") supports the PUT verb and supplies the target requestedId.
     */
    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        if (!cashCardRepository.existsByIdAndOwner(requestedId, principal.getName())) {
            return ResponseEntity.notFound().build();
        }

        final CashCard updatedCashCard = new CashCard(requestedId, cashCardUpdate.amount(), principal.getName());
        cashCardRepository.save(updatedCashCard);
        return ResponseEntity.noContent().build();
    }

    /**
     * @apiNote @DeleteMapping("/{id}") supports the DELETE verb and supplies the target id.
     */
    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            return ResponseEntity.notFound().build();
        }

        cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
