export const formatCurrency = (amount) => {
  const absoluteAmount = Math.abs(amount);
  const formatted = new Intl.NumberFormat('en-NG', {
    style: 'currency',
    currency: 'NGN',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(absoluteAmount);
  
  return amount < 0 ? `-${formatted}` : formatted;
};

export const formatCurrencyForInput = (amount) => {
  return Math.abs(amount).toFixed(2);
};
