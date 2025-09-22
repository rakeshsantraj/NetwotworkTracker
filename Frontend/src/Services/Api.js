import axios from "axios";
import {
  MOCK_PACKETS,
  MOCK_COUNTS,
  MOCK_TRAFFIC,
} from "./mockData";

const BASE_URL = process.env.REACT_APP_BASE_URL;
// const BASE_URL = 'http://3.99.207.184:5000';

const delay = (data, ms = 300) =>
  new Promise((resolve) => setTimeout(() => resolve(data), ms));

export const fetchPackets = async (filters = {}) => {
  try {
    const res = await axios.get(`${BASE_URL}/api/packets`, { params: filters });
    return res.data;
  } catch (err) {
    console.warn("Using mock packets:", err.message);
    return delay(MOCK_PACKETS);
  }
};

export const fetchProtocolCounts = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/api/summary`);
    return res.data;
  } catch (err) {
    console.warn("Using mock counts:", err.message);
    return delay(MOCK_COUNTS);
  }
};

export const fetchTraffic = async () => {
  try {
    const res = await axios.get(`${BASE_URL}/traffic`);
    return res.data;
  } catch (err) {
    console.warn("Using mock traffic:", err.message);
    return delay(MOCK_TRAFFIC);
  }
};
