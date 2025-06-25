# Use a small Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy everything to /app
COPY . .

# Give permission to mvnw (only if not already done)
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
