package com.infernalwhaler.cashcard.repository;

import com.infernalwhaler.cashcard.model.CashCard;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Sdeseure
 * @project cashcard
 * @date 9/05/2025
 */

public interface CashCardRepository extends CrudRepository<CashCard, Long> {
}
