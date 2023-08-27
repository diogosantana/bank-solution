FROM maven:3.9-eclipse-temurin-17-alpine as builder
WORKDIR /build
COPY pom.xml /build
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn -DfinalName=bank-solution package

FROM openjdk:17-alpine
WORKDIR /opt/bank-solution
COPY --from=builder /build/target/bank-solution.jar /opt/bank-solution/
#ENTRYPOINT [ "/bin/sh" ]
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "bank-solution.jar" ]