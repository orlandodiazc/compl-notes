FROM maven:3-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=build /target/*.jar app.jar
COPY src/test/java/com/ditod/notes/fixtures/images images
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar","app.jar"]

