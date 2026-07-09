# Backend multi-stage Dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY back/pom.xml ./
COPY back/src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
