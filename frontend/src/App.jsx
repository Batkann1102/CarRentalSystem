import { useEffect, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Toaster, toast } from 'react-hot-toast';
import AppShell from './components/AppShell';
import DashboardPage from './pages/DashboardPage';
import CarsPage from './pages/CarsPage';
import CarEditPage from './pages/CarEditPage';
import CarRentPage from './pages/CarRentPage';

function App() {
  const [theme, setTheme] = useState(() => localStorage.getItem('theme') || 'dark');

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }, [theme]);

  function handleToggleTheme() {
    setTheme((current) => (current === 'dark' ? 'light' : 'dark'));
  }

  function handleNotify(type, message) {
    toast[type](message);
  }

  return (
    <BrowserRouter>
      <Toaster
        position="top-right"
        toastOptions={{
          style: {
            borderRadius: '16px',
            background: theme === 'dark' ? '#13203a' : '#ffffff',
            color: theme === 'dark' ? '#eef4ff' : '#122033',
            border: theme === 'dark' ? '1px solid rgba(148, 163, 184, 0.18)' : '1px solid rgba(37, 99, 235, 0.16)'
          }
        }}
      />
      <Routes>
        <Route
          path="/"
          element={<AppShell theme={theme} onToggleTheme={handleToggleTheme} onNotify={handleNotify} />}
        >
          <Route index element={<DashboardPage onNotify={handleNotify} />} />
          <Route path="cars" element={<CarsPage onNotify={handleNotify} />} />
          <Route path="cars/:carId/edit" element={<CarEditPage onNotify={handleNotify} />} />
          <Route path="cars/:carId/rent" element={<CarRentPage onNotify={handleNotify} />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;

