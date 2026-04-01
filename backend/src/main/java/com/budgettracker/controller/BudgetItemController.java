package com.budgettracker.controller;

import com.budgettracker.dto.budget.BudgetItemRequest;
import com.budgettracker.dto.budget.BudgetItemResponse;
import com.budgettracker.dto.budget.BudgetSummaryResponse;
import com.budgettracker.dto.budget.MonthlyValueRequest;
import com.budgettracker.dto.budget.MonthlyValueResponse;
import com.budgettracker.service.BudgetItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget-plans")
public class BudgetItemController {

    @Autowired
    private BudgetItemService budgetItemService;

    @PostMapping("/{planId}/items")
    public ResponseEntity<BudgetItemResponse> createItem(
            @PathVariable Long planId,
            @Valid @RequestBody BudgetItemRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        BudgetItemResponse response = budgetItemService.createBudgetItem(userId, planId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{planId}/items/{itemId}")
    public ResponseEntity<BudgetItemResponse> updateItem(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            @Valid @RequestBody BudgetItemRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        BudgetItemResponse response = budgetItemService.updateBudgetItem(userId, planId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{planId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        budgetItemService.deleteBudgetItem(userId, planId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{planId}/items/{itemId}/monthly-values")
    public ResponseEntity<MonthlyValueResponse> updateMonthlyValue(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            @Valid @RequestBody MonthlyValueRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        MonthlyValueResponse response = budgetItemService.updateMonthlyValue(userId, planId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{planId}/summary")
    public ResponseEntity<BudgetSummaryResponse> getBudgetSummary(
            @PathVariable Long planId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        BudgetSummaryResponse response = budgetItemService.getBudgetSummary(userId, planId);
        return ResponseEntity.ok(response);
    }
}
