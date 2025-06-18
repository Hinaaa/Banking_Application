FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml backend/
COPY backend/src backend/src
RUN mvn -f backend/pom.xml clean package -DskipTests

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/backend/target/*.jar bankingapplication.jar
CMD ["java", "-jar", "bankingapplication.jar"]