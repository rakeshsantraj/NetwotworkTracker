import React from "react";
import { NavLink } from "react-router-dom";

const Sidebar = () => (
  <aside className="sidebar" aria-label="Main navigation">
    <div className="brand">
      <div className="logo">🛰️</div>
      <div className="brand-title">Packet Analyzer</div>
    </div>

    <nav>
      <ul>
        <li>
          <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav-active" : ""}>
            <span className="nav-icon">📊</span> Dashboard
          </NavLink>
        </li>
        <li>
          <NavLink to="/charts" className={({ isActive }) => isActive ? "nav-active" : ""}>
            <span className="nav-icon">📈</span> Charts
          </NavLink>
        </li>
        <li>
          <NavLink to="/packets" className={({ isActive }) => isActive ? "nav-active" : ""}>
            <span className="nav-icon">📑</span> Packets
          </NavLink>
        </li>
     
      </ul>
    </nav>
  </aside>
);

export default Sidebar;
