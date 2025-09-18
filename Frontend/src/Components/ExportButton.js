
import React from "react";
import { toPng } from "html-to-image";
import { saveAs } from "file-saver";
import jsPDF from "jspdf";

export function exportCSV(packets) {
  const rows = [
    ["id", "timestamp", "protocol", "srcIP", "srcPort", "destIP", "destPort", "length", "anomaly", "score", "note"],
    ...packets.map(p => [p.id, p.timestamp, p.protocol, p.srcIP, p.srcPort, p.destIP, p.destPort, p.length, p.anomaly, p.classificationScore, p.note])
  ];
  const csv = rows.map(r => r.map(c => `"${String(c).replace(/"/g, '""')}"`).join(",")).join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  saveAs(blob, "packets.csv");
}

export async function exportPDFFromNode(elementId, filename = "report.pdf") {
  const node = document.getElementById(elementId);
  if (!node) {
    alert("No element to export");
    return;
  }
  try {
    const dataUrl = await toPng(node, { cacheBust: true });
    const pdf = new jsPDF("landscape");
    const imgProps = pdf.getImageProperties(dataUrl);
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;
    pdf.addImage(dataUrl, "PNG", 0, 10, pdfWidth, pdfHeight);
    pdf.save(filename);
  } catch (e) {
    console.error(e);
    alert("Failed to generate PDF: " + e.message);
  }
}

export function shareJSON(packets) {
  const payload = { exportedAt: new Date().toISOString(), packets };
  navigator.clipboard?.writeText(JSON.stringify(payload, null, 2))
    .then(() => alert("Export JSON copied to clipboard"))
    .catch(() => alert("Could not copy to clipboard"));
}
