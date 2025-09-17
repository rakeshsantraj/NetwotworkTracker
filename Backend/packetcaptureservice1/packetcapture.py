import asyncio
import json
import requests
from scapy.all import sniff, IP, TCP, UDP
from threading import Thread
from queue import Queue
import datetime

# URL of your Spring Boot Producer service
PRODUCER_URL = "http://localhost:8081/produce"

# Shared queue between sniffer and async worker
packet_queue = Queue()

# Headers for JSON POST
headers = {"Content-Type": "application/json"}

# Log file
LOG_FILE = "captured_packets_async.log"

# -------------------------------
# Utility: write logs to file
# -------------------------------
def log_to_file(message: str):
    with open(LOG_FILE, "a") as f:
        f.write(f"{datetime.datetime.now()} | {message}\n")

# -------------------------------
# 1. Callback: runs when new packet arrives
# -------------------------------
def handle_packet(packet):
    try:
        data = {
            "src_ip": packet[IP].src if IP in packet else None,
            "dst_ip": packet[IP].dst if IP in packet else None,
            "protocol": "TCP" if TCP in packet else "UDP" if UDP in packet else "OTHER",
            "length": len(packet),
        }
        packet_queue.put(data)  # push into queue
        msg = f"📥 Captured packet → {data}"
        print(msg)
        log_to_file(msg)
    except Exception as e:
        err = f"⚠️ Error handling packet: {e}"
        print(err)
        log_to_file(err)

# -------------------------------
# 2. Worker: sends packets to Spring Boot Producer
# -------------------------------
async def worker():
    loop = asyncio.get_event_loop()
    while True:
        if not packet_queue.empty():
            packet = packet_queue.get()
            try:
                await loop.run_in_executor(
                    None,
                    lambda: requests.post(PRODUCER_URL, data=json.dumps(packet), headers=headers, timeout=3)
                )
                msg = f"📤 Sent packet → {packet}"
                print(msg)
                log_to_file(msg)
            except Exception as e:
                err = f"❌ Failed to send packet: {e}"
                print(err)
                log_to_file(err)
        await asyncio.sleep(0.1)

# -------------------------------
# 3. Start sniffer in background
# -------------------------------
def start_sniffer():
    sniff(prn=handle_packet, store=False)

# -------------------------------
# 4. Main entry
# -------------------------------
def main():
    sniffer_thread = Thread(target=start_sniffer, daemon=True)
    sniffer_thread.start()
    asyncio.run(worker())

if __name__ == "__main__":
    main()
