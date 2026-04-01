package com.budgettracker.repository;

import com.budgettracker.entity.BudgetItem;
import com.budgettracker.entity.MonthlyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyValueRepository extends JpaRepository<MonthlyValue, Long> {
    List<MonthlyValue> findByBudgetItem(BudgetItem budgetItem);
    
    Optional<MonthlyValue> findByBudgetItemAndMonth(BudgetItem budgetItem, Integer month);
    
    @Query("SELECT mv FROM MonthlyValue mv WHERE mv.budgetItem.budgetPlan.id = :planId ORDER BY mv.budgetItem.groupType, mv.budgetItem.sortOrder, mv.month")
    List<MonthlyValue> findByBudgetPlanIdOrderByItemAndMonth(@Param("planId") Long planId);
}
