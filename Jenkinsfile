pipeline {
    agent any

    tools {
        maven 'maven-3.8.7'
        jdk 'jdk-17'
    }

    environment {
        REGISTRY = "athul9thd"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
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
                SONAR_SCANNER = tool 'sonar-scanner'
            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([
                        string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')
                    ]) {
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
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                    #!/bin/bash
                    set -e

                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                    SERVICES=(
                      spring-petclinic-admin-server
                      spring-petclinic-customers-service
                      spring-petclinic-vets-service
                      spring-petclinic-visits-service
                      spring-petclinic-genai-service
                      spring-petclinic-config-server
                      spring-petclinic-discovery-server
                      spring-petclinic-api-gateway
                    )

                    for SERVICE in "${SERVICES[@]}"; do
                      echo "Building $SERVICE"
                      docker build -t $REGISTRY/$SERVICE:4.0.1-$IMAGE_TAG $SERVICE
                      docker push $REGISTRY/$SERVICE:4.0.1-$IMAGE_TAG
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
