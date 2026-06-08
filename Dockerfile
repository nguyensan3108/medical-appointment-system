# STAGE 1: BUILDER
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven configurations first to leverage Docker layer caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Grant execution permissions to the Maven wrapper
RUN chmod +x mvnw

# Download dependencies offline to optimize subsequent build times
RUN ./mvnw dependency:go-offline

# Copy the application source code and package the artifact
COPY src ./src
RUN ./mvnw clean package -DskipTests

#STAGE 2: RUNNER
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the compiled JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application listens on
EXPOSE 8080

# Configure the  container startup execution command
ENTRYPOINT ["java", "-jar", "app.jar"]