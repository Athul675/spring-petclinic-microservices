pipeline {
    agent any

    tools {
        jdk 'jdk-17'
        maven 'maven-3.8.7'
    }

    environment {
        DOCKERHUB_USERNAME = 'athul9thd'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout Source') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Athul675/spring-petclinic-microservices.git',
                    credentialsId: 'github-pat'
            }
        }

        stage('Maven Build') {
            steps {
                sh '''
                  mvn clean package -DskipTests
                '''
            }
        }

        stage('SonarQube Scan') {
            environment {
                SONAR_TOKEN = credentials('sonar-token')
            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                      /opt/sonar-scanner/bin/sonar-scanner \
                        -Dsonar.projectKey=spring-petclinic-microservices \
                        -Dsonar.projectName=spring-petclinic-microservices \
                        -Dsonar.sources=. \
                        -Dsonar.java.binaries=**/target/classes \
                        -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    sh '''
                      echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''

                    sh '''
                      SERVICES=(
                        spring-petclinic-admin-server:9090
                        spring-petclinic-config-server:8888
                        spring-petclinic-discovery-server:8761
                        spring-petclinic-api-gateway:8080
                        spring-petclinic-customers-service:8081
                        spring-petclinic-visits-service:8082
                        spring-petclinic-vets-service:8083
                        spring-petclinic-genai-service:8084
                      )

                      for SERVICE in "${SERVICES[@]}"; do
                        NAME=$(echo $SERVICE | cut -d: -f1)
                        PORT=$(echo $SERVICE | cut -d: -f2)

                        echo "Building image for $NAME"

                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=$NAME/target/$NAME-4.0.1 \
                          --build-arg EXPOSED_PORT=$PORT \
                          -t $DOCKERHUB_USERNAME/$NAME:$IMAGE_TAG .

                        docker push $DOCKERHUB_USERNAME/$NAME:$IMAGE_TAG
                      done
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
