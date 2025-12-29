FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=target/spring-petclinic-vets-service-4.0.1.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","/app/app.jar"]
