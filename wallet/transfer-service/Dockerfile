FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY wallet-common ./wallet-common
COPY deposit-service ./deposit-service
COPY transaction-service ./transaction-service
COPY withdrawal-service ./withdrawal-service
COPY transfer-service ./transfer-service
RUN mvn -pl transfer-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/transfer-service/target/*-SNAPSHOT.jar app.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]