import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { budgetApi } from '../api/budgetApi';
import { formatCurrency, formatCurrencyForInput } from '../utils/formatCurrency';

const BudgetDetailPage = () => {
  const { id } = useParams();
  const [budgetSummary, setBudgetSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editingCell, setEditingCell] = useState(null);

  useEffect(() => {
    fetchBudgetSummary();
  }, [id]);

  const fetchBudgetSummary = async () => {
    try {
      const summary = await budgetApi.getBudgetSummary(id);
      setBudgetSummary(summary);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch budget summary');
    } finally {
      setLoading(false);
    }
  };

  const handleCellChange = async (itemId, month, newAmount) => {
    try {
      const amount = parseFloat(newAmount) || 0;
      await budgetApi.updateMonthlyValue(id, itemId, { month, amount });
      
      // Optimistic update
      setBudgetSummary(prev => {
        const updatedItems = prev.items.map(item => {
          if (item.id === itemId) {
            const updatedMonthlyAmounts = [...item.monthlyAmounts];
            const sign = (item.groupType === 'INFLOW') ? 1 : -1;
            updatedMonthlyAmounts[month - 1] = amount * sign;
            
            const newRowTotal = updatedMonthlyAmounts.reduce((sum, amt) => sum + amt, 0);
            
            return {
              ...item,
              monthlyAmounts: updatedMonthlyAmounts,
              rowTotal: newRowTotal
            };
          }
          return item;
        });
        
        const updatedMonthlyTotals = [...prev.monthlyNetTotals];
        updatedMonthlyTotals[month - 1] = updatedItems.reduce((sum, item) => 
          sum + item.monthlyAmounts[month - 1], 0
        );
        
        const newGrandTotal = updatedMonthlyTotals.reduce((sum, total) => sum + total, 0);
        
        return {
          ...prev,
          items: updatedItems,
          monthlyNetTotals: updatedMonthlyTotals,
          grandTotal: newGrandTotal
        };
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update value');
      fetchBudgetSummary(); // Refresh on error
    }
    
    setEditingCell(null);
  };

  const handleItemNameChange = async (itemId, newName) => {
    try {
      const item = budgetSummary.items.find(item => item.id === itemId);
      await budgetApi.updateBudgetItem(id, itemId, { 
        name: newName, 
        sortOrder: item.sortOrder 
      });
      
      setBudgetSummary(prev => ({
        ...prev,
        items: prev.items.map(item => 
          item.id === itemId ? { ...item, name: newName } : item
        )
      }));
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update item name');
    }
  };

  const handleDeleteItem = async (itemId) => {
    if (window.confirm('Are you sure you want to delete this budget item?')) {
      try {
        await budgetApi.deleteBudgetItem(id, itemId);
        fetchBudgetSummary();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to delete item');
      }
    }
  };

  const handleAddItem = async (groupType) => {
    const itemName = window.prompt(`Enter name for new ${groupType.replace('_', ' ')} item:`);
    if (itemName) {
      try {
        await budgetApi.createBudgetItem(id, { 
          name: itemName, 
          groupType, 
          sortOrder: 0 
        });
        fetchBudgetSummary();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to add item');
      }
    }
  };

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const groupTypes = ['INFLOW', 'FIXED_EXPENSE', 'VARIABLE_COST'];

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (!budgetSummary) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-red-600">Budget not found</div>
      </div>
    );
  }

  return (
    <div className="max-w-full mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">{budgetSummary.name}</h1>
          <p className="text-gray-600">Budget for {budgetSummary.year}</p>
        </div>

        {error && (
          <div className="mb-4 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded">
            {error}
          </div>
        )}

        <div className="overflow-x-auto bg-white shadow rounded-lg">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  ITEMS
                </th>
                {months.map((month, index) => (
                  <th key={index} className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {month}
                  </th>
                ))}
                <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  TOTAL
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {groupTypes.map(groupType => {
                const groupItems = budgetSummary.items.filter(item => item.groupType === groupType);
                if (groupItems.length === 0) return null;
                
                return (
                  <React.Fragment key={groupType}>
                    <tr className="bg-gray-100">
                      <td colSpan="14" className="px-6 py-3 text-left text-sm font-medium text-gray-900">
                        {groupType.replace('_', ' ')}
                      </td>
                    </tr>
                    {groupItems.map(item => (
                      <tr key={item.id} className={`hover:bg-gray-50 ${item.groupType === 'INFLOW' ? 'bg-green-50' : 'bg-red-50'}`}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          <div className="flex items-center justify-between">
                            <input
                              type="text"
                              value={item.name}
                              onChange={(e) => handleItemNameChange(item.id, e.target.value)}
                              className="bg-transparent border-none focus:outline-none focus:ring-2 focus:ring-primary-500 rounded px-1"
                            />
                            <button
                              onClick={() => handleDeleteItem(item.id)}
                              className="text-red-600 hover:text-red-800 ml-2"
                            >
                              🗑️
                            </button>
                          </div>
                        </td>
                        {item.monthlyAmounts.map((amount, monthIndex) => (
                          <td key={monthIndex} className="px-6 py-4 whitespace-nowrap text-sm text-center">
                            {editingCell?.itemId === item.id && editingCell?.month === monthIndex + 1 ? (
                              <input
                                type="number"
                                step="0.01"
                                defaultValue={formatCurrencyForInput(amount)}
                                onBlur={(e) => handleCellChange(item.id, monthIndex + 1, e.target.value)}
                                onKeyPress={(e) => {
                                  if (e.key === 'Enter') {
                                    e.target.blur();
                                  }
                                }}
                                className="w-20 px-2 py-1 border border-primary-300 rounded focus:outline-none focus:ring-2 focus:ring-primary-500 text-center"
                                autoFocus
                              />
                            ) : (
                              <div
                                onClick={() => setEditingCell({ itemId: item.id, month: monthIndex + 1 })}
                                className="cursor-pointer hover:bg-gray-100 px-2 py-1 rounded"
                              >
                                {formatCurrency(amount)}
                              </div>
                            )}
                          </td>
                        ))}
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-center font-medium">
                          {formatCurrency(item.rowTotal)}
                        </td>
                      </tr>
                    ))}
                    <tr>
                      <td colSpan="13" className="px-6 py-2 text-right">
                        <button
                          onClick={() => handleAddItem(groupType)}
                          className="text-primary-600 hover:text-primary-800 text-sm font-medium"
                        >
                          + Add {groupType.replace('_', ' ')} Item
                        </button>
                      </td>
                      <td></td>
                    </tr>
                  </React.Fragment>
                );
              })}
              <tr className="bg-gray-200 font-bold">
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  NET TOTAL
                </td>
                {budgetSummary.monthlyNetTotals.map((total, index) => (
                  <td key={index} className={`px-6 py-4 whitespace-nowrap text-sm text-center font-medium ${total < 0 ? 'text-red-600' : 'text-green-600'}`}>
                    {formatCurrency(total)}
                  </td>
                ))}
                <td className={`px-6 py-4 whitespace-nowrap text-sm text-center font-bold ${budgetSummary.grandTotal < 0 ? 'text-red-600' : 'text-green-600'}`}>
                  {formatCurrency(budgetSummary.grandTotal)}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default BudgetDetailPage;
