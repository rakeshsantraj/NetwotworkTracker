import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from "recharts";

const TrafficLine = ({ data }) => (
  <div className="card">
    <h4>Traffic Trend</h4>
    <ResponsiveContainer width="100%" height={350}>
      <LineChart
        data={data}
        margin={{ top: 20, right: 30, left: 20, bottom: 60 }} 
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis
          dataKey="time"
          interval={0} 
          tick={{ fill: "#e4e6ef", fontSize: 12 }}
          angle={0} 
          textAnchor="middle" 
          height={60} 
        />
        <YAxis
          allowDecimals={false}
          tick={{ fill: "#e4e6ef", fontSize: 12 }}
        />
        <Tooltip
          labelStyle={{ color: "#000" }}
          contentStyle={{ backgroundColor: "#fff", borderRadius: "6px" }}
        />
        <Line
          type="monotone"
          dataKey="packets"
          stroke="#4a90e2"
          strokeWidth={2}
          dot={false}
        />
      </LineChart>
    </ResponsiveContainer>
  </div>
);

export default TrafficLine;
