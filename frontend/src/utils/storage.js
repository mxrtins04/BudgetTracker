export const getUserFromStorage = () => {
  try {
    const token = localStorage.getItem('budget_token');
    const userData = localStorage.getItem('budget_user');
    
    if (token && userData) {
      return JSON.parse(userData);
    }
    return null;
  } catch (error) {
    console.error('Error parsing user data:', error);
    return null;
  }
};

export const saveUserToStorage = (user, token) => {
  localStorage.setItem('budget_token', token);
  const userData = {
    userId: user.userId,
    email: user.email,
    fullName: user.fullName
  };
  localStorage.setItem('budget_user', JSON.stringify(userData));
};

export const clearUserStorage = () => {
  localStorage.removeItem('budget_token');
  localStorage.removeItem('budget_user');
};
