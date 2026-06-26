FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /backend
COPY --from=build /app/target/courier-project-0.0.1-SNAPSHOT.jar /backend
CMD ["java", "-jar", "courier-project-0.0.1-SNAPSHOT.jar"]