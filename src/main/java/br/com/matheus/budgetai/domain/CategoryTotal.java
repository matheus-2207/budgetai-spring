package br.com.matheus.budgetai.domain;
import java.math.BigDecimal;
public record CategoryTotal(Category category, long count, BigDecimal total) { }
