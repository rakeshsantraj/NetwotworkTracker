import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import DashboardPage from "./Pages/DashboardPage";
import PacketsPage from "./Pages/PacketsPage";
import ChartsPage from "./Pages/ChartsPage";

const App = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard"/>} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/charts" element={<ChartsPage />} />
      <Route path="/packets" element={<PacketsPage />} />
    </Routes>
  </BrowserRouter>
);

export default App;
