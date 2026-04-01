import axiosInstance from './axiosInstance';

export const budgetApi = {
  // Budget Plans
  getBudgetPlans: async () => {
    const response = await axiosInstance.get('/budget-plans');
    return response.data;
  },

  createBudgetPlan: async (planData) => {
    const response = await axiosInstance.post('/budget-plans', planData);
    return response.data;
  },

  getBudgetPlan: async (planId) => {
    const response = await axiosInstance.get(`/budget-plans/${planId}`);
    return response.data;
  },

  deleteBudgetPlan: async (planId) => {
    await axiosInstance.delete(`/budget-plans/${planId}`);
  },

  // Budget Items
  createBudgetItem: async (planId, itemData) => {
    const response = await axiosInstance.post(`/budget-plans/${planId}/items`, itemData);
    return response.data;
  },

  updateBudgetItem: async (planId, itemId, itemData) => {
    const response = await axiosInstance.put(`/budget-plans/${planId}/items/${itemId}`, itemData);
    return response.data;
  },

  deleteBudgetItem: async (planId, itemId) => {
    await axiosInstance.delete(`/budget-plans/${planId}/items/${itemId}`);
  },

  updateMonthlyValue: async (planId, itemId, monthData) => {
    const response = await axiosInstance.patch(`/budget-plans/${planId}/items/${itemId}/monthly-values`, monthData);
    return response.data;
  },

  getBudgetSummary: async (planId) => {
    const response = await axiosInstance.get(`/budget-plans/${planId}/summary`);
    return response.data;
  },
};
