import React, { useState } from "react";
import { saveAs } from "file-saver";

const PacketTable = ({ packets = [] }) => {
  const [page, setPage] = useState(0);
  const pageSize = 8;

  const totalPages = Math.max(1, Math.ceil(packets.length / pageSize));
  const pageData = packets.slice(page * pageSize, page * pageSize + pageSize);

  const exportCSV = () => {
    const rows = [
      ["ID", "Timestamp", "Protocol", "SrcIP", "DestIP", "SrcPort", "DestPort", "Length","Info"],
      ...packets.map(p => [
        p.id,
        new Date(p.timestamp).toLocaleString(),
        p.protocol,
        p.srcIP,
        p.destIP,
        p.srcPort,
        p.destPort,
        p.length,
        p.info
      ])
    ];
    const csv = rows.map(r => r.join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    saveAs(blob, "packets.csv");
  };

  return (
    <div>
      <div className="table-top">
        <button className="btn" onClick={exportCSV}>Export CSV</button>
      </div>

      <table className="packet-table">
        <thead>
          <tr>
            <th>ID</th><th>Timestamp</th><th>Protocol</th>
            <th>Src IP</th><th>Dest IP</th>
            <th>Src Port</th><th>Dest Port</th><th>Length</th><th>Info</th>
          </tr>
        </thead>
        <tbody>
          {pageData.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{new Date(p.timestamp).toLocaleString()}</td>
              <td>{p.protocol}</td>
              <td>{p.srcIP}</td>
              <td>{p.destIP}</td>
              <td>{p.srcPort}</td>
              <td>{p.destPort}</td>
              <td>{p.length}</td>
              <td>{p.info}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="pagination">
        <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>Prev</button>
        <span> Page {page + 1} of {totalPages} </span>
        <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1}>Next</button>
      </div>
    </div>
  );
};

export default PacketTable;
