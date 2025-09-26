import React, { useState, useEffect } from "react";
import ProtocolPie from "../Components/ProtocolPie";
import TrafficLine from "../Components/TrafficLine";
import Loader from "../Components/Loader";
import PacketTable from "../Components/PacketTable";
import StatsPanel from "../Components/StatsPanel";
import {
  fetchPackets,
  fetchProtocolCounts,
  fetchTraffic,
  startSniffing,
  stopSniffing,
  fetchStatus
} from "../Services/Api";

const DashboardPage = () => {
  const [packets, setPackets] = useState([]);
  const [counts, setCounts] = useState({});
  const [traffic, setTraffic] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCapturing, setIsCapturing] = useState(false);
  const [iface, setIface] = useState("");

  const loadData = async () => {
    setLoading(true);
    try {
      const [pkt, cnt, trf] = await Promise.all([
        fetchPackets(),
        fetchProtocolCounts(),
        fetchTraffic(),
      ]);
      setPackets(pkt);
      setCounts(cnt);
      setTraffic(trf);
    } catch (err) {
      console.error("Failed to load data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleStartCapture = async () => {
    try {
      await startSniffing();
      setIsCapturing(true);

     const status = await fetchStatus();
     setIface(status.iface || "");

     await loadData();
    } 
     catch (err) {
     console.error("Start capture failed:", err);
    }
  };


  const handleStopCapture = async () => {
     try {
      await stopSniffing();
      setIsCapturing(false);
      setIface("");
      await loadData();
    } 
    catch (err) {
      console.error("Stop capture failed:", err);
    }
  };

const handleClearAll = () => {
  setPackets([]);
  setCounts({});
  setTraffic([]);
  setIsCapturing(false);
  setIface("");
};


  return (
    <div className="dashboard">
      {/* Header */}
      <header className="header-bar">
        <div className="header-left">
          <h1>Packet Analyzer Dashboard</h1>
          <p>Network Traffic Monitor</p>
        </div>
        <div className="header-right">
          <div className="stat-box">
            PACKETS CAPTURED <b>{packets?.content?.length}</b>
          </div>
          <div className={`stat-box ${isCapturing ? "green" : "yellow"}`}>
            {isCapturing ? "CAPTURING" : "READY TO CAPTURE"}
          </div>
          <div className="stat-box">
            CAPTURE PROGRESS {packets?.content?.length} packets stored
          </div>
        </div>
      </header>

      {/* Capture Control */}
      <section className="card capture-control">
        <h2>Capture Control</h2>
        <div className="control-row">
          <label>Packet Limit:</label>
          <input type="number" defaultValue={500} />
          <button className="btn green" onClick={handleStartCapture}>
            ▶ Start Capture
          </button>
          <button className="btn gray" onClick={handleStopCapture}>
            ⏹ Stop Capture
          </button>
          <button className="btn orange" onClick={handleClearAll}>
            🗑 Clear All ({packets?.content?.length})
          </button>
        </div>
        <div className="status-row">
          <span>
            Service Status: <b className="green">Connected</b>
          </span>
          <span>
            Capture Status: <b>{isCapturing ? `Active (Interface: ${iface})` : "Inactive"}</b>
          </span>
          <span>
            Packets Captured: <b>{packets?.content?.length} / 500</b>
          </span>
          <span>
            Live Count: <b>{packets?.content?.length}</b>
          </span>
        </div>
      </section>

      {/* Table + Stats */}
      <section className="content-grid">
        <div className="card wide" id="packetTable">
          {loading ? <Loader /> : <PacketTable packets={packets} />}
        </div>
        <StatsPanel packets={packets} counts={counts} />
      </section>

      {/* Protocol Cards */}

      <section className="protocol-cards">
        {Object.entries(counts).map(([protocol, count]) => (
           <div key={protocol} className={`protocol-card ${protocol.toLowerCase()}`}>
             <span className="protocol-name">{protocol}</span>
             <span className="protocol-value">{count}</span>
           </div>
          ))}
      </section>


      {/* Charts */}
      <section className="charts-grid">
        <div className="protocol-breakdown card">
          <h3>Protocol Breakdown</h3>
          <ProtocolPie counts={counts} />
        </div>
        <div className="traffic-over-time card">
          <h3>Traffic Over Time</h3>
          <TrafficLine data={traffic} />
        </div>
      </section>
    </div>
  );
};

export default DashboardPage;
