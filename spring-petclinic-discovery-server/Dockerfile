FROM eclipse-temurin:17-jre
WORKDIR /app
# Wildcard ensures the build doesn't fail on version string changes
COPY target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]
