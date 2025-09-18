# Use an official OpenJDK image from the Docker Hub
FROM openjdk:17-jdk-slim
# Set the working directory inside the container
WORKDIR /app
# Install PostgreSQL client
RUN apt-get update && \
    apt-get install -y --no-install-recommends postgresql-client && \
    rm -rf /var/lib/apt/lists/*
RUN ls
COPY target/packetanalyserservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8782
# Command to run the jar file
CMD ["java", "-jar", "app.jar"]
