package com.infernalwhaler.cashcard.model;

import org.springframework.data.annotation.Id;

/**
 * @author Sdeseure
 * @project cash card
 * @date 8/05/2025
 */

public record CashCard(@Id Long id, Double amount, String owner) {

}
