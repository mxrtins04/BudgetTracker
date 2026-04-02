package com.budgettracker.service;

import com.budgettracker.dto.budget.BudgetItemRequest;
import com.budgettracker.dto.budget.BudgetItemResponse;
import com.budgettracker.dto.budget.BudgetSummaryResponse;
import com.budgettracker.dto.budget.MonthlyValueRequest;
import com.budgettracker.dto.budget.MonthlyValueResponse;
import com.budgettracker.entity.*;
import com.budgettracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetItemService {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private BudgetPlanRepository budgetPlanRepository;

    @Autowired
    private MonthlyValueRepository monthlyValueRepository;

    public BudgetItemResponse createBudgetItem(Long userId, Long planId, BudgetItemRequest request) {
        BudgetPlan budgetPlan = getBudgetPlanAndValidateOwnership(userId, planId);

        BudgetItem item = new BudgetItem();
        item.setBudgetPlan(budgetPlan);
        item.setName(request.getName());
        item.setGroupType(request.getGroupType());
        item.setSortOrder(request.getSortOrder());

        item = budgetItemRepository.save(item);

        for (int month = 1; month <= 12; month++) {
            MonthlyValue monthlyValue = new MonthlyValue();
            monthlyValue.setBudgetItem(item);
            monthlyValue.setMonth(month);
            monthlyValue.setAmount(BigDecimal.ZERO);
            monthlyValueRepository.save(monthlyValue);
        }

        return convertToResponse(item);
    }

    public BudgetItemResponse updateBudgetItem(Long userId, Long planId, Long itemId, BudgetItemRequest request) {
        BudgetItem item = getBudgetItemAndValidateOwnership(userId, planId, itemId);

        item.setName(request.getName());
        item.setSortOrder(request.getSortOrder());

        item = budgetItemRepository.save(item);

        return convertToResponse(item);
    }

    public void deleteBudgetItem(Long userId, Long planId, Long itemId) {
        BudgetItem item = getBudgetItemAndValidateOwnership(userId, planId, itemId);
        budgetItemRepository.delete(item);
    }

    public MonthlyValueResponse updateMonthlyValue(Long userId, Long planId, Long itemId, MonthlyValueRequest request) {
        BudgetItem item = getBudgetItemAndValidateOwnership(userId, planId, itemId);

        MonthlyValue monthlyValue = monthlyValueRepository
                .findByBudgetItemAndMonth(item, request.getMonth())
                .orElseThrow(() -> new IllegalArgumentException("Monthly value not found"));

        BigDecimal amount = request.getAmount();
        if (item.getGroupType() == GroupType.FIXED_EXPENSE || item.getGroupType() == GroupType.VARIABLE_COST) {
            amount = amount.negate();
        }

        monthlyValue.setAmount(amount);
        monthlyValue = monthlyValueRepository.save(monthlyValue);

        return convertToResponse(monthlyValue);
    }

    public BudgetSummaryResponse getBudgetSummary(Long userId, Long planId) {
        BudgetPlan budgetPlan = getBudgetPlanAndValidateOwnership(userId, planId);

        List<BudgetItem> items = budgetItemRepository.findByBudgetPlanOrderByGroupTypeAndSortOrder(budgetPlan);

        BudgetSummaryResponse response = new BudgetSummaryResponse();
        response.setPlanId(budgetPlan.getId());
        response.setName(budgetPlan.getName());
        response.setYear(budgetPlan.getYear());

        List<BudgetSummaryResponse.BudgetItemSummary> itemSummaries = new ArrayList<>();
        BigDecimal[] monthlyTotals = new BigDecimal[12];
        Arrays.fill(monthlyTotals, BigDecimal.ZERO);

        for (BudgetItem item : items) {
            BudgetSummaryResponse.BudgetItemSummary itemSummary = new BudgetSummaryResponse.BudgetItemSummary();
            itemSummary.setId(item.getId());
            itemSummary.setName(item.getName());
            itemSummary.setGroupType(item.getGroupType());

            List<BigDecimal> monthlyAmounts = new ArrayList<>();
            BigDecimal rowTotal = BigDecimal.ZERO;

            for (int month = 1; month <= 12; month++) {
                MonthlyValue mv = monthlyValueRepository.findByBudgetItemAndMonth(item, month).orElse(null);
                if (mv == null) {
                    mv = new MonthlyValue();
                    mv.setBudgetItem(item);
                    mv.setMonth(month);
                    mv.setAmount(BigDecimal.ZERO);
                }

                BigDecimal amount = mv.getAmount();
                monthlyAmounts.add(amount);
                rowTotal = rowTotal.add(amount);
                monthlyTotals[month - 1] = monthlyTotals[month - 1].add(amount);
            }

            itemSummary.setMonthlyAmounts(monthlyAmounts);
            itemSummary.setRowTotal(rowTotal);
            itemSummaries.add(itemSummary);
        }

        response.setItems(itemSummaries);
        response.setMonthlyNetTotals(Arrays.asList(monthlyTotals));

        BigDecimal grandTotal = Arrays.stream(monthlyTotals).reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setGrandTotal(grandTotal);

        return response;
    }

    private BudgetPlan getBudgetPlanAndValidateOwnership(Long userId, Long planId) {
        BudgetPlan budgetPlan = budgetPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Budget plan not found"));

        if (!budgetPlan.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        return budgetPlan;
    }

    private BudgetItem getBudgetItemAndValidateOwnership(Long userId, Long planId, Long itemId) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Budget item not found"));

        if (!item.getBudgetPlan().getId().equals(planId)) {
            throw new IllegalArgumentException("Budget item does not belong to this plan");
        }

        if (!item.getBudgetPlan().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        return item;
    }

    private BudgetItemResponse convertToResponse(BudgetItem item) {
        BudgetItemResponse response = new BudgetItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setGroupType(item.getGroupType());
        response.setSortOrder(item.getSortOrder());

        List<MonthlyValueResponse> monthlyValues = monthlyValueRepository.findByBudgetItem(item).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        response.setMonthlyValues(monthlyValues);
        return response;
    }

    private MonthlyValueResponse convertToResponse(MonthlyValue monthlyValue) {
        MonthlyValueResponse response = new MonthlyValueResponse();
        response.setId(monthlyValue.getId());
        response.setMonth(monthlyValue.getMonth());
        response.setAmount(monthlyValue.getAmount());
        return response;
    }
}
