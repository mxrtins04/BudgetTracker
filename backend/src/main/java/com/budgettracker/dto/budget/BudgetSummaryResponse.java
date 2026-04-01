package com.budgettracker.dto.budget;

import com.budgettracker.entity.GroupType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BudgetSummaryResponse {
    private Long planId;
    private String name;
    private Integer year;
    private List<BudgetItemSummary> items;
    private List<BigDecimal> monthlyNetTotals;
    private BigDecimal grandTotal;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<BudgetItemSummary> getItems() {
        return items;
    }

    public void setItems(List<BudgetItemSummary> items) {
        this.items = items;
    }

    public List<BigDecimal> getMonthlyNetTotals() {
        return monthlyNetTotals;
    }

    public void setMonthlyNetTotals(List<BigDecimal> monthlyNetTotals) {
        this.monthlyNetTotals = monthlyNetTotals;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    @Data
    public static class BudgetItemSummary {
        private Long id;
        private String name;
        private GroupType groupType;
        private List<BigDecimal> monthlyAmounts;
        private BigDecimal rowTotal;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public GroupType getGroupType() {
            return groupType;
        }

        public void setGroupType(GroupType groupType) {
            this.groupType = groupType;
        }

        public List<BigDecimal> getMonthlyAmounts() {
            return monthlyAmounts;
        }

        public void setMonthlyAmounts(List<BigDecimal> monthlyAmounts) {
            this.monthlyAmounts = monthlyAmounts;
        }

        public BigDecimal getRowTotal() {
            return rowTotal;
        }

        public void setRowTotal(BigDecimal rowTotal) {
            this.rowTotal = rowTotal;
        }
    }
}
