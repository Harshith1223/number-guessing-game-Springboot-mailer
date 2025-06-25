# 1. Use Maven image to build the jar
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Use JDK image to run the app
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# 3. Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
