FROM eclipse-temurin:17-jre
WORKDIR /app
# Using wildcard to ensure build works regardless of exact version string
COPY target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]
