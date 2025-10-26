# Stage 1: Build the JAR
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jdk
# Set working directory
WORKDIR /app
# Copy the built JAR file into the container
COPY --from=builder /app/target/*.jar deviceApi.jar
# Copy SSL files into the runtime container
COPY src/main/resources/ssl/ /app/ssl/

# Expose the internal port your app runs on
EXPOSE 8081

LABEL authors="piotr"

# Run the application
ENTRYPOINT ["java", "-jar", "deviceApi.jar"]
