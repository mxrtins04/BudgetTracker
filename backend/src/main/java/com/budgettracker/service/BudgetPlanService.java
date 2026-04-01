package com.budgettracker.service;

import com.budgettracker.dto.budget.BudgetPlanRequest;
import com.budgettracker.dto.budget.BudgetPlanResponse;
import com.budgettracker.entity.BudgetPlan;
import com.budgettracker.entity.BudgetItem;
import com.budgettracker.entity.GroupType;
import com.budgettracker.entity.MonthlyValue;
import com.budgettracker.entity.User;
import com.budgettracker.repository.BudgetPlanRepository;
import com.budgettracker.repository.BudgetItemRepository;
import com.budgettracker.repository.MonthlyValueRepository;
import com.budgettracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetPlanService {
    
    @Autowired
    private BudgetPlanRepository budgetPlanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetItemRepository budgetItemRepository;
    
    @Autowired
    private MonthlyValueRepository monthlyValueRepository;
    
    public List<BudgetPlanResponse> getUserBudgetPlans(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return budgetPlanRepository.findByUser(user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public BudgetPlanResponse createBudgetPlan(Long userId, BudgetPlanRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (budgetPlanRepository.existsByUserAndYear(user, request.getYear())) {
            throw new IllegalArgumentException("Budget plan already exists for year " + request.getYear());
        }
        
        BudgetPlan budgetPlan = new BudgetPlan();
        budgetPlan.setUser(user);
        budgetPlan.setName(request.getName());
        budgetPlan.setYear(request.getYear());
        
        budgetPlan = budgetPlanRepository.save(budgetPlan);
        
        // Seed default items
        seedDefaultItems(budgetPlan);
        
        return convertToResponse(budgetPlan);
    }
    
    public BudgetPlanResponse getBudgetPlan(Long userId, Long planId) {
        BudgetPlan budgetPlan = getBudgetPlanAndValidateOwnership(userId, planId);
        return convertToResponse(budgetPlan);
    }
    
    public void deleteBudgetPlan(Long userId, Long planId) {
        BudgetPlan budgetPlan = getBudgetPlanAndValidateOwnership(userId, planId);
        budgetPlanRepository.delete(budgetPlan);
    }
    
    private void seedDefaultItems(BudgetPlan budgetPlan) {
        String[] defaultItems = {
            "Salary", "Other Income", "Rent", "Data", "Emergency Fund", 
            "Savings", "Miscellaneous", "Electricity", "Feeding", 
            "Transportation", "Loan"
        };
        
        GroupType[] groupTypes = {
            GroupType.INFLOW, GroupType.INFLOW, GroupType.FIXED_EXPENSE, 
            GroupType.FIXED_EXPENSE, GroupType.FIXED_EXPENSE, GroupType.FIXED_EXPENSE,
            GroupType.FIXED_EXPENSE, GroupType.FIXED_EXPENSE, GroupType.VARIABLE_COST,
            GroupType.VARIABLE_COST, GroupType.VARIABLE_COST
        };
        
        for (int i = 0; i < defaultItems.length; i++) {
            BudgetItem item = new BudgetItem();
            item.setBudgetPlan(budgetPlan);
            item.setName(defaultItems[i]);
            item.setGroupType(groupTypes[i]);
            item.setSortOrder(i);
            
            item = budgetItemRepository.save(item);
            
            // Create 12 monthly values for each item
            for (int month = 1; month <= 12; month++) {
                MonthlyValue monthlyValue = new MonthlyValue();
                monthlyValue.setBudgetItem(item);
                monthlyValue.setMonth(month);
                monthlyValue.setAmount(BigDecimal.ZERO);
                monthlyValueRepository.save(monthlyValue);
            }
        }
    }
    
    private BudgetPlan getBudgetPlanAndValidateOwnership(Long userId, Long planId) {
        BudgetPlan budgetPlan = budgetPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Budget plan not found"));
        
        if (!budgetPlan.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }
        
        return budgetPlan;
    }
    
    private BudgetPlanResponse convertToResponse(BudgetPlan budgetPlan) {
        BudgetPlanResponse response = new BudgetPlanResponse();
        response.setId(budgetPlan.getId());
        response.setName(budgetPlan.getName());
        response.setYear(budgetPlan.getYear());
        response.setCreatedAt(budgetPlan.getCreatedAt());
        response.setItemCount(budgetPlan.getBudgetItems() != null ? budgetPlan.getBudgetItems().size() : 0);
        return response;
    }
}
