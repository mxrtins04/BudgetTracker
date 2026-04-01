import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { budgetApi } from '../api/budgetApi';

const DashboardPage = () => {
  const [budgetPlans, setBudgetPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newPlan, setNewPlan] = useState({ name: '', year: new Date().getFullYear() });
  const { user } = useAuth();

  useEffect(() => {
    fetchBudgetPlans();
  }, []);

  const fetchBudgetPlans = async () => {
    try {
      const plans = await budgetApi.getBudgetPlans();
      setBudgetPlans(plans);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch budget plans');
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePlan = async (e) => {
    e.preventDefault();
    try {
      await budgetApi.createBudgetPlan(newPlan);
      setShowCreateModal(false);
      setNewPlan({ name: '', year: new Date().getFullYear() });
      fetchBudgetPlans();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create budget plan');
    }
  };

  const handleDeletePlan = async (planId) => {
    if (window.confirm('Are you sure you want to delete this budget plan?')) {
      try {
        await budgetApi.deleteBudgetPlan(planId);
        fetchBudgetPlans();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to delete budget plan');
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Welcome back, {user?.fullName}!
          </h1>
          <p className="mt-2 text-gray-600">
            Manage your budget plans and track your financial goals.
          </p>
        </div>

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded">
            {error}
          </div>
        )}

        <div className="mb-6">
          <button
            onClick={() => setShowCreateModal(true)}
            className="bg-primary-600 hover:bg-primary-700 text-white font-medium py-2 px-4 rounded transition duration-200"
          >
            Create New Budget
          </button>
        </div>

        {budgetPlans.length === 0 ? (
          <div className="text-center py-12">
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              No budget plans yet
            </h3>
            <p className="text-gray-600 mb-4">
              Create your first budget plan to get started with tracking your finances.
            </p>
            <button
              onClick={() => setShowCreateModal(true)}
              className="bg-primary-600 hover:bg-primary-700 text-white font-medium py-2 px-4 rounded transition duration-200"
            >
              Create Budget Plan
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {budgetPlans.map((plan) => (
              <div
                key={plan.id}
                className="bg-white overflow-hidden shadow rounded-lg hover:shadow-lg transition-shadow duration-200"
              >
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-medium text-gray-900">{plan.name}</h3>
                    <span className="text-sm text-gray-500">{plan.year}</span>
                  </div>
                  <p className="text-gray-600 mb-4">
                    {plan.itemCount} budget items
                  </p>
                  <div className="flex justify-between">
                    <Link
                      to={`/budget/${plan.id}`}
                      className="text-primary-600 hover:text-primary-500 font-medium"
                    >
                      View Details
                    </Link>
                    <button
                      onClick={() => handleDeletePlan(plan.id)}
                      className="text-red-600 hover:text-red-500 font-medium"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {showCreateModal && (
          <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
            <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
              <h3 className="text-lg font-bold text-gray-900 mb-4">
                Create New Budget Plan
              </h3>
              <form onSubmit={handleCreatePlan}>
                <div className="mb-4">
                  <label className="block text-gray-700 text-sm font-bold mb-2">
                    Budget Name
                  </label>
                  <input
                    type="text"
                    required
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    value={newPlan.name}
                    onChange={(e) => setNewPlan({ ...newPlan, name: e.target.value })}
                    placeholder="e.g., 2026 Budget"
                  />
                </div>
                <div className="mb-4">
                  <label className="block text-gray-700 text-sm font-bold mb-2">
                    Year
                  </label>
                  <input
                    type="number"
                    required
                    min="2000"
                    max="2100"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                    value={newPlan.year}
                    onChange={(e) => setNewPlan({ ...newPlan, year: parseInt(e.target.value) })}
                  />
                </div>
                <div className="flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => setShowCreateModal(false)}
                    className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
                  >
                    Create
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;
