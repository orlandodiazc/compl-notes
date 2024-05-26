FROM maven:3-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
COPY --from=build /target/*.jar app.jar
COPY src/test/java/com/ditod/notes/fixtures/images images
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","app.jar"]


