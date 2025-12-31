FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
# Match the containerPort in your K8s deployment
EXPOSE 8080 
ENTRYPOINT ["java","-jar","app.jar"]
