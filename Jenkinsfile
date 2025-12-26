pipeline {
    agent any

    tools {
        maven 'maven-3.8.7'
        jdk 'jdk-17'
    }

    environment {
        DOCKERHUB_USERNAME = 'athul9thd'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Athul675/spring-petclinic-microservices.git',
                    credentialsId: 'github-pat'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
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
                      -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''
                }
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                sh '''
                set -e

                build_and_push () {
                  SERVICE=$1
                  JAR=$2
                  PORT=$3

                  docker build -f docker/Dockerfile \
                    --build-arg ARTIFACT_NAME=$JAR \
                    --build-arg EXPOSED_PORT=$PORT \
                    -t $DOCKERHUB_USERNAME/$SERVICE:$IMAGE_TAG .

                  docker push $DOCKERHUB_USERNAME/$SERVICE:$IMAGE_TAG
                }

                build_and_push spring-petclinic-admin-server \
                  spring-petclinic-admin-server/target/spring-petclinic-admin-server-4.0.1 \
                  9090

                build_and_push spring-petclinic-config-server \
                  spring-petclinic-config-server/target/spring-petclinic-config-server-4.0.1 \
                  8888

                build_and_push spring-petclinic-discovery-server \
                  spring-petclinic-discovery-server/target/spring-petclinic-discovery-server-4.0.1 \
                  8761

                build_and_push spring-petclinic-api-gateway \
                  spring-petclinic-api-gateway/target/spring-petclinic-api-gateway-4.0.1 \
                  8080

                build_and_push spring-petclinic-customers-service \
                  spring-petclinic-customers-service/target/spring-petclinic-customers-service-4.0.1 \
                  8081

                build_and_push spring-petclinic-visits-service \
                  spring-petclinic-visits-service/target/spring-petclinic-visits-service-4.0.1 \
                  8082

                build_and_push spring-petclinic-vets-service \
                  spring-petclinic-vets-service/target/spring-petclinic-vets-service-4.0.1 \
                  8083

                build_and_push spring-petclinic-genai-service \
                  spring-petclinic-genai-service/target/spring-petclinic-genai-service-4.0.1 \
                  8084
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
