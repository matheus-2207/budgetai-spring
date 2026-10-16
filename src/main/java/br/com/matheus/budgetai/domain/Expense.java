package br.com.matheus.budgetai.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Expense(UUID id, String description, BigDecimal amount, Category category,
                      LocalDate occurredOn, Instant createdAt) {
    public Expense {
        if (id == null || occurredOn == null || createdAt == null || category == null)
            throw new IllegalArgumentException("Identificador, categoria e datas são obrigatórios.");
        if (description == null || description.isBlank() || description.strip().length() > 200)
            throw new IllegalArgumentException("Descrição deve ter de 1 a 200 caracteres.");
        description = description.strip();
        if (amount == null || amount.signum() <= 0 || amount.compareTo(new BigDecimal("999999999.99")) > 0)
            throw new IllegalArgumentException("Valor deve ser positivo e no máximo 999999999.99 reais.");
        try { amount = amount.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Valor deve ter no máximo duas casas decimais.");
        }
    }
}
