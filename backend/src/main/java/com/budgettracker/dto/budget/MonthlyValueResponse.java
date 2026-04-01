package com.budgettracker.dto.budget;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyValueResponse {
    private Long id;
    private Integer month;
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
