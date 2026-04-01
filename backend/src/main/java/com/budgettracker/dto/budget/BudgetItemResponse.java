package com.budgettracker.dto.budget;

import com.budgettracker.entity.GroupType;
import lombok.Data;

import java.util.List;

@Data
public class BudgetItemResponse {
    private Long id;
    private String name;
    private GroupType groupType;
    private Integer sortOrder;
    private List<MonthlyValueResponse> monthlyValues;

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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<MonthlyValueResponse> getMonthlyValues() {
        return monthlyValues;
    }

    public void setMonthlyValues(List<MonthlyValueResponse> monthlyValues) {
        this.monthlyValues = monthlyValues;
    }
}
