package com.infernalwhaler.cashcard.repository;

import com.infernalwhaler.cashcard.model.CashCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sdeseure
 * @project cash card
 * @date 9/05/2025
 */

public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
}
