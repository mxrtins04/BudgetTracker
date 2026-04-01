import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';
import { getUserFromStorage, saveUserToStorage, clearUserStorage } from '../utils/storage';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = () => {
      const user = getUserFromStorage();
      setUser(user);
      setLoading(false);
    };
    
    initializeAuth();
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authApi.login(credentials);
      saveUserToStorage(response, response.token);
      setUser({
        userId: response.userId,
        email: response.email,
        fullName: response.fullName
      });
      return response;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      const response = await authApi.register(userData);
      const user = {
        userId: response.userId,
        email: response.email,
        fullName: response.fullName
      };
      saveUserToStorage(user, response.token);
      setUser(user);
      return response;
    } catch (error) {
      console.error('Registration error:', error);
      if (error.response) {
        const errorMessage = error.response.data?.message || 
                         error.response.data?.error || 
                         `Server error: ${error.response.status}`;
        throw new Error(errorMessage);
      }
      throw error;
    }
  };

  const logout = () => {
    clearUserStorage();
    setUser(null);
  };

  const value = {
    user,
    login,
    register,
    logout,
    loading,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
