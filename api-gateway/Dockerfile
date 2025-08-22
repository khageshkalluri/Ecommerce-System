FROM maven:3.9.11-eclipse-temurin-17  as builder

WORKDIR ./app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

FROM openjdk:17-jdk as runner

WORKDIR /app

COPY --from=builder ./app/target/api-gateway-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 6004

ENTRYPOINT ["java","-jar","app.jar"]


