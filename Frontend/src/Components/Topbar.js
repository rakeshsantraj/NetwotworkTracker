import React from "react";
import { useLocation } from "react-router-dom";

const pageTitles = {
  "/dashboard": { title: "Dashboard", icon: "📊" },
  "/charts": { title: "Charts", icon: "📈" },
  "/packets": { title: "Packets", icon: "📑" },
};

const Topbar = () => {
  const location = useLocation();
  const page = pageTitles[location.pathname] || {
    title: "Network Packet Analysis",
    icon: "🛰️",
  };

  return (
    <div className="topbar">
      <div className="top-left" style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
        <span className="topbar-icon" style={{ fontSize: "1.3rem" }}>
          {page.icon}
        </span>
        <h1>{page.title}</h1>
      </div>
    </div>
  );
};

export default Topbar;
