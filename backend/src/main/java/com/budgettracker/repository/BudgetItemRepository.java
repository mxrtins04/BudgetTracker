package com.budgettracker.repository;

import com.budgettracker.entity.BudgetItem;
import com.budgettracker.entity.BudgetPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {
    List<BudgetItem> findByBudgetPlan(BudgetPlan budgetPlan);
    
    @Query("SELECT bi FROM BudgetItem bi WHERE bi.budgetPlan = :budgetPlan ORDER BY bi.groupType, bi.sortOrder")
    List<BudgetItem> findByBudgetPlanOrderByGroupTypeAndSortOrder(BudgetPlan budgetPlan);
}
