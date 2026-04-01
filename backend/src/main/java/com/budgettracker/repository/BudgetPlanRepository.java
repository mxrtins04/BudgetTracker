package com.budgettracker.repository;

import com.budgettracker.entity.BudgetPlan;
import com.budgettracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetPlanRepository extends JpaRepository<BudgetPlan, Long> {
    List<BudgetPlan> findByUser(User user);
    Optional<BudgetPlan> findByUserAndYear(User user, Integer year);
    boolean existsByUserAndYear(User user, Integer year);
}
