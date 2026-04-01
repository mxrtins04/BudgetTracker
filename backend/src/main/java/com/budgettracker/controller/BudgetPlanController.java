package com.budgettracker.controller;

import com.budgettracker.dto.budget.BudgetPlanRequest;
import com.budgettracker.dto.budget.BudgetPlanResponse;
import com.budgettracker.service.BudgetPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget-plans")
public class BudgetPlanController {
    
    @Autowired
    private BudgetPlanService budgetPlanService;
    
    @GetMapping
    public ResponseEntity<List<BudgetPlanResponse>> getAllPlans(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<BudgetPlanResponse> plans = budgetPlanService.getUserBudgetPlans(userId);
        return ResponseEntity.ok(plans);
    }
    
    @PostMapping
    public ResponseEntity<BudgetPlanResponse> createPlan(
            @Valid @RequestBody BudgetPlanRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        BudgetPlanResponse response = budgetPlanService.createBudgetPlan(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BudgetPlanResponse> getPlan(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        BudgetPlanResponse response = budgetPlanService.getBudgetPlan(userId, id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        budgetPlanService.deleteBudgetPlan(userId, id);
        return ResponseEntity.noContent().build();
    }
}
