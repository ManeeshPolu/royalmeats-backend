FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
RUN chmod +x mvnw
COPY src src
RUN ./mvnw -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV PORT=8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
