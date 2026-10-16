package br.com.matheus.budgetai.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Period(LocalDate from, LocalDate to) {
    public Period {
        if (from == null || to == null || from.isAfter(to))
            throw new IllegalArgumentException("Informe um período com início menor ou igual ao fim.");
        if (ChronoUnit.DAYS.between(from, to) > 365)
            throw new IllegalArgumentException("O período deve ter no máximo 366 dias, incluindo as extremidades.");
    }
}
