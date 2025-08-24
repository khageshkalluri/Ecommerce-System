FROM maven:3.9.11-eclipse-temurin-17 as Builder

WORKDIR ./app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

From openjdk:17 as RUNNER

WORKDIR /app

COPY --from=Builder ./app/target/Auth-Service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 2000

ENTRYPOINT ["java","-jar","app.jar"]

