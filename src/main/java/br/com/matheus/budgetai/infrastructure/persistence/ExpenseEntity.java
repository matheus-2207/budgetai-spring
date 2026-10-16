package br.com.matheus.budgetai.infrastructure.persistence;

import br.com.matheus.budgetai.domain.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = @Index(name = "idx_expense_date_category", columnList = "occurred_on,category"))
public class ExpenseEntity {
    @Id private UUID id;
    @Column(nullable = false, length = 200) private String description;
    @Column(nullable = false, precision = 11, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Category category;
    @Column(name = "occurred_on", nullable = false) private LocalDate occurredOn;
    @Column(nullable = false) private Instant createdAt;
    protected ExpenseEntity() { }
    public ExpenseEntity(Expense expense) {
        id = expense.id(); description = expense.description(); amount = expense.amount();
        category = expense.category(); occurredOn = expense.occurredOn(); createdAt = expense.createdAt();
    }
    public Expense toDomain() { return new Expense(id, description, amount, category, occurredOn, createdAt); }
}
