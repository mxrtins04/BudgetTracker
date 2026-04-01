package com.budgettracker.dto.budget;

import com.budgettracker.entity.GroupType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BudgetItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 255, message = "Item name must be between 2 and 255 characters")
    private String name;

    private GroupType groupType;

    private Integer sortOrder = 0;

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
}
