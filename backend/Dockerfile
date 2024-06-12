FROM maven:3-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

ARG SPRING_ACTIVE_PROFILE

FROM eclipse-temurin:17-jdk
COPY --from=build /target/*.jar app.jar
COPY src/test/java/com/ditod/notes/fixtures/images images
EXPOSE 8080
ENTRYPOINT ["java", "-jar","app.jar"]


