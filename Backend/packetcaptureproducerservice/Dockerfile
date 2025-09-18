# Use an official OpenJDK image from the Docker Hub
FROM openjdk:17-jdk-slim
# Set the working directory inside the container
WORKDIR /app

RUN ls
COPY target/packetcaptureproducerservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8781
# Command to run the jar file
CMD ["java", "-jar", "app.jar"]
