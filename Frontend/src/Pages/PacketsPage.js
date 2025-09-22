import React, { useState, useEffect } from "react";
import Sidebar from "../Components/Sidebar";
import Topbar from "../Components/Topbar";
import Loader from "../Components/Loader";
import { fetchPackets } from "../Services/Api";
import { saveAs } from "file-saver";

const PacketsPage = () => {
  const [packets, setPackets] = useState([]);
  const [protocol, setProtocol] = useState("");
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const pageSize = 4; 

  useEffect(() => {
    loadPackets();
  }, [protocol]);

  const loadPackets = async () => {
    setLoading(true);
    try {
      const data = await fetchPackets();
      setPackets(data);
      setPage(0);
    } catch (error) {
      console.error("Failed to load packets:", error);
      setPackets([]);
    } finally {
      setLoading(false);
    }
  };

  const exportCSV = () => {
    const rows = [
      ["id", "timestamp", "protocol", "srcIP", "destIP", "srcPort", "destPort", "length"],
      ...filtered.map((p) => [
        p.id,
        p.timestamp,
        p.protocol,
        p.srcIP,
        p.destIP,
        p.srcPort,
        p.destPort,
        p.length,
      ]),
    ];
    const csv = rows.map((r) => r.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    saveAs(blob, "packets.csv");
  };

  const filtered = packets
    .filter((p) => (protocol ? p.protocol === protocol : true))
    .filter((p) => {
      const q = search.trim().toLowerCase();
      if (!q) return true;
      return (
        p.protocol.toLowerCase().includes(q) ||
        p.srcIP.toLowerCase().includes(q) ||
        p.destIP.toLowerCase().includes(q) ||
        (p.labels || []).join(" ").toLowerCase().includes(q) ||
        (p.note || "").toLowerCase().includes(q)
      );
    });

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const pageData = filtered.slice(page * pageSize, page * pageSize + pageSize);

  return (
    <div className="app-root">
      <Sidebar />
      <div className="main-pane">
        <Topbar title="Packets" />
        <main className="main-content">
          <div className="table-top">
            <input
              placeholder="Search in table..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
            />
            <select value={protocol} onChange={(e) => setProtocol(e.target.value)}>
              <option value="">All Protocols</option>
              <option value="TCP">TCP</option>
              <option value="UDP">UDP</option>
              <option value="HTTP">HTTP</option>
              <option value="DNS">DNS</option>
            </select>
            <button className="btn" onClick={exportCSV}>
              Export CSV
            </button>
          </div>

          {loading ? (
            <Loader />
          ) : (
            <>
              <table className="packet-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Timestamp</th>
                    <th>Protocol</th>
                    <th>Src IP</th>
                    <th>Dest IP</th>
                    <th>Src Port</th>
                    <th>Dest Port</th>
                    <th>Length</th>
                    <th>Info</th>
                  </tr>
                </thead>
                <tbody>
                  {pageData.map((packet) => (
                    <tr key={packet.id} className={packet.anomaly ? "anomaly-row" : ""}>
                      <td>{packet.id}</td>
                      <td>{packet.timestamp}</td>
                      <td>{packet.protocol}</td>
                      <td>{packet.srcIP}</td>
                      <td>{packet.destIP}</td>
                      <td>{packet.srcPort}</td>
                      <td>{packet.destPort}</td>
                      <td>{packet.length}</td>
                      <td>{packet.info}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="pagination">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={page === 0}
                >
                  Prev
                </button>
                <span> Page {page + 1} of {totalPages} </span>
                <button
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                >
                  Next
                </button>
              </div>
            </>
          )}
        </main>
      </div>
    </div>
  );
};

export default PacketsPage;
