FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

ENV MAVEN_OPTS="-Xmx256m -XX:MaxMetaspaceSize=64m"

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["sh","-c","java -XX:+UseSerialGC -XX:MaxRAMPercentage=50.0 -XX:MaxMetaspaceSize=100m -Xss256k -Dserver.port=$PORT -jar /app/app.jar"]