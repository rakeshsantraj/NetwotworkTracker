import React, { useEffect, useState } from "react";
import Sidebar from "../Components/Sidebar";
import Topbar from "../Components/Topbar";
import Loader from "../Components/Loader";
import { fetchProtocolCounts } from "../Services/Api";

const DashboardPage = () => {
  const [counts, setCounts] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        const data = await fetchProtocolCounts();
        setCounts(data);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  return (
    <div className="app-root">
      <Sidebar />
      <div className="main-pane">
        <Topbar />
        <main className="main-content">
          {loading ? <Loader /> : (
            <div className="count-row">
              {Object.entries(counts).map(([protocol, value]) => (
                <div key={protocol} className={`protocol-card ${protocol.toLowerCase()}`}>
                  <span className="protocol-name">{protocol}</span>
                  <span className="protocol-value">{value}</span>
                </div>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default DashboardPage;
