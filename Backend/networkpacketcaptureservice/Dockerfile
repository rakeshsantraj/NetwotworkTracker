# Use an official OpenJDK image from the Docker Hub
FROM openjdk:17-jdk-slim
# Set the working directory inside the container
WORKDIR /app
RUN apt-get update && apt-get install -y iputils-ping curl netcat libpcap-dev net-tools && rm -rf /var/lib/apt/lists/*
RUN ls
COPY target/networkpacketcaptureservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8785
# Command to run the jar file
CMD ["java", "-jar", "app.jar"]
