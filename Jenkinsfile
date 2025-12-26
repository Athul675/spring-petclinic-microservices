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
                    mvn sonar:sonar \
                      -Dsonar.login=$SONAR_TOKEN \
                      -Dsonar.projectKey=spring-petclinic-microservices \
                      -Dsonar.projectName=spring-petclinic-microservices
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

                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                    sh '''
                    build_and_push () {
                      SERVICE=$1
                      PORT=$2
                      IMAGE=$3

                      JAR=$(ls $SERVICE/target/*.jar | grep -v original | sed 's/.jar$//')

                      docker build -f docker/Dockerfile \
                        --build-arg ARTIFACT_NAME=$JAR \
                        --build-arg EXPOSED_PORT=$PORT \
                        -t $DOCKERHUB_USERNAME/$IMAGE:$IMAGE_TAG .

                      docker push $DOCKERHUB_USERNAME/$IMAGE:$IMAGE_TAG
                    }

                    build_and_push spring-petclinic-admin-server      9090 spring-petclinic-admin-server
                    build_and_push spring-petclinic-config-server     8888 spring-petclinic-config-server
                    build_and_push spring-petclinic-discovery-server  8761 spring-petclinic-discovery-server
                    build_and_push spring-petclinic-api-gateway       8080 spring-petclinic-api-gateway
                    build_and_push spring-petclinic-customers-service 8081 spring-petclinic-customers-service
                    build_and_push spring-petclinic-visits-service    8082 spring-petclinic-visits-service
                    build_and_push spring-petclinic-vets-service      8083 spring-petclinic-vets-service
                    build_and_push spring-petclinic-genai-service     8084 spring-petclinic-genai-service
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
