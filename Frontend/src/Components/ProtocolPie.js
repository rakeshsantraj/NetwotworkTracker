import React from "react";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";

const COLORS = ["#3182ce", "#48bb78", "#ecc94b", "#ed8936"];

const ProtocolPie = ({ counts }) => {
  const data = counts
    ? Object.entries(counts).map(([name, value]) => ({ name, value }))
    : [];

  const total = data.reduce((sum, d) => sum + d.value, 0);

  return (
    <div className="card">
      <h4>Protocol Counts</h4>
      <ResponsiveContainer width="100%" height={340}>
        <PieChart>
          <Pie
            data={data}
            dataKey="value"
            nameKey="name"
            outerRadius={110}
            label={({ name, value }) => {
              const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
              return `${value} (${percent}%)`;
            }}
            labelLine={false}
          >
            {data.map((_, i) => (
              <Cell key={i} fill={COLORS[i % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip formatter={(value, name) => [`${value}`, `${name}`]} />
          <Legend
            verticalAlign="bottom"
            align="center"
            wrapperStyle={{ marginTop: "20px" }}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ProtocolPie;
