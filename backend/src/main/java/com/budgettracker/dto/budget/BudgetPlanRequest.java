package com.budgettracker.dto.budget;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BudgetPlanRequest {

    @NotBlank(message = "Budget plan name is required")
    @Size(min = 2, max = 255, message = "Budget plan name must be between 2 and 255 characters")
    private String name;

    @Min(value = 2000, message = "Year must be 2000 or later")
    private Integer year;

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
}
