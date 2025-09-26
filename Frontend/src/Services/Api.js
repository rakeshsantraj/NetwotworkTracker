import axios from "axios";
import { MOCK_PACKETS, MOCK_COUNTS, MOCK_TRAFFIC, MOCK_STATUS } from "./mockData";

const BASE_URL = process.env.REACT_APP_BASE_URL || 'http://15.223.211.204:8783';
const BASE_URL1 = process.env.REACT_APP_BASE_URL1 || 'http://15.223.211.204:8785';
// const BASE_URL = "http://localhost:5000";

const delay = (data, ms = 300) =>
  new Promise((resolve) => setTimeout(() => resolve(data), ms));

export const fetchPackets = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/api/packets`);
    return res.data;
  } catch (err) {
    console.warn("Using mock packets:", err.message);
    return delay(MOCK_PACKETS);
  }
};

export const fetchPacketsperPage = async (page = 0, size = 10) => {
  try {
    const res = await axios.get(`${BASE_URL}/api/packets`, {
      params: { page, size },
    });
    return res.data;
  } catch (err) {
    console.warn("Using mock packets:", err.message);
    return delay(MOCK_PACKETS);
  }
};

export const fetchProtocolCounts = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/api/packetcounts`);
    return res.data;
  } catch (err) {
    console.warn("Using mock counts:", err.message);
    return delay(MOCK_COUNTS);
  }
};

export const fetchTraffic = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/api/graph`);
    return res.data;
  } catch (err) {
    console.warn("Using mock traffic:", err.message);
    return delay(MOCK_TRAFFIC);
  }
};

export const startSniffing = async () => {
  try {
    const res = await axios.post(`${BASE_URL1}/api/v1/sniffer/start`);
    return res.data;
  } catch (err) {
    console.error("Failed to start sniffing:", err.message);
    throw err;
  }
};

export const stopSniffing = async () => {
  try {
    const res = await axios.post(`${BASE_URL1}/api/v1/sniffer/stop`);
    return res.data;
  } catch (err) {
    console.error("Failed to stop sniffing:", err.message);
    throw err;
  }
};


export const fetchStatus = async () => {
  try {
    const res = await axios.get(`${BASE_URL1}/api/v1/sniffer/status`);
    return res.data;
  } catch (err) {
    console.warn("Using mock status:", err.message);
    return delay(MOCK_STATUS);
  }
};

