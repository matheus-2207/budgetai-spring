package br.com.matheus.budgetai.infrastructure.http;

import br.com.matheus.budgetai.application.*;
import br.com.matheus.budgetai.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService service;
    public ExpenseController(ExpenseService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<Expense> create(@RequestBody CreateExpense input) {
        return ResponseEntity.status(201).body(service.create(input));
    }
    @GetMapping
    public List<Expense> list(@RequestParam LocalDate from, @RequestParam LocalDate to,
                              @RequestParam(required = false) Category category) {
        return service.list(from, to, category);
    }
    @GetMapping("/summary")
    public ExpenseSummary summary(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return service.summarize(from, to);
    }
}
