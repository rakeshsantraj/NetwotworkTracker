# Use official Python image
FROM python:3.11-slim

# Install OS dependencies for scapy and networking
RUN apt-get update && apt-get install -y \
    tcpdump \
    libpcap-dev \
    iproute2 \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy your Python script
COPY packetcapture.py .

# Set default command
CMD ["python", "packetcapture.py"]
