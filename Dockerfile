FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml backend/
COPY backend/src backend/src
# Copy React build from frontend-build
COPY --from=frontend-build /app/frontend/build src/main/resources/static
RUN mvn clean package -DskipTests

FROM openjdk:21
WORKDIR /app
COPY --from=backend-build /app/backend/target/*.jar bankingapplication.jar
CMD ["java", "-jar", "bankingapplication.jar"]