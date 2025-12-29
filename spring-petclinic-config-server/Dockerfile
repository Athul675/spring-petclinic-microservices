FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=target/spring-petclinic-config-server-4.0.1.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8888

ENTRYPOINT ["java","-jar","/app/app.jar"]
