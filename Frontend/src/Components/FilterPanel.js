import React, { useState } from "react";

export default function FilterPanel({ onApply }) {
  const [protocol, setProtocol] = useState("");
  const [query, setQuery] = useState("");
  const [port, setPort] = useState("");
  const [timeFrom, setTimeFrom] = useState("");
  const [timeTo, setTimeTo] = useState("");

  function apply() {
    onApply({ protocol, query, port: port || undefined, timeFrom: timeFrom || undefined, timeTo: timeTo || undefined });
  }

  function reset() {
    setProtocol(""); setQuery(""); setPort(""); setTimeFrom(""); setTimeTo("");
    onApply({});
  }

  return (
    <div className="filter-bar" role="search">
      <input placeholder="Search by IP / protocol / label" aria-label="Search" value={query} onChange={e => setQuery(e.target.value)} />
      <select aria-label="Protocol" value={protocol} onChange={e => setProtocol(e.target.value)}>
        <option value="">All Protocols</option>
        <option>TCP</option>
        <option>UDP</option>
        <option>HTTP</option>
        <option>DNS</option>
      </select>
      <input placeholder="Port" aria-label="Port" value={port} onChange={e => setPort(e.target.value)} />
      <label>From <input type="datetime-local" value={timeFrom} onChange={e => setTimeFrom(e.target.value)} /></label>
      <label>To <input type="datetime-local" value={timeTo} onChange={e => setTimeTo(e.target.value)} /></label>
      <button className="btn" onClick={apply}>Apply</button>
      <button className="btn" onClick={reset}>Reset</button>
    </div>
  );
}
