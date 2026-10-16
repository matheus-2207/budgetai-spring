package br.com.matheus.budgetai.application;

import br.com.matheus.budgetai.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ExpenseService {
    private final ExpenseRepository repository;
    private final Clock clock;
    public ExpenseService(ExpenseRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }
    @Transactional
    public Expense create(CreateExpense input) {
        if (input == null) throw new IllegalArgumentException("Despesa é obrigatória.");
        if (input.occurredOn() != null && input.occurredOn().isAfter(LocalDate.now(clock)))
            throw new IllegalArgumentException("A data da despesa não pode estar no futuro.");
        return repository.save(new Expense(UUID.randomUUID(), input.description(), input.amount(),
                input.category(), input.occurredOn(), clock.instant()));
    }
    public List<Expense> list(LocalDate from, LocalDate to, Category category) {
        return repository.find(new Period(from, to), category);
    }
    public ExpenseSummary summarize(LocalDate from, LocalDate to) {
        var period = new Period(from, to);
        var groups = repository.summarize(period);
        return new ExpenseSummary(from, to, groups.stream().mapToLong(CategoryTotal::count).sum(),
                groups.stream().map(CategoryTotal::total).reduce(new BigDecimal("0.00"), BigDecimal::add), groups);
    }
}
