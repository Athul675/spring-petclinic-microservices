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
                          -Dsonar.login=$SONAR_TOKEN
                    '''
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
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-admin-server/target/spring-petclinic-admin-server-4.0.1 \
                          --build-arg EXPOSED_PORT=9090 \
                          -t athul9thd/spring-petclinic-admin-server:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-admin-server:$IMAGE_TAG
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-config-server/target/spring-petclinic-config-server-4.0.1 \
                          --build-arg EXPOSED_PORT=8888 \
                          -t athul9thd/spring-petclinic-config-server:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-config-server:$IMAGE_TAG
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-discovery-server/target/spring-petclinic-discovery-server-4.0.1 \
                          --build-arg EXPOSED_PORT=8761 \
                          -t athul9thd/spring-petclinic-discovery-server:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-discovery-server:$IMAGE_TAG
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-api-gateway/target/spring-petclinic-api-gateway-4.0.1 \
                          --build-arg EXPOSED_PORT=8080 \
                          -t athul9thd/spring-petclinic-api-gateway:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-api-gateway:$IMAGE_TAG
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-customers-service/target/spring-petclinic-customers-service-4.0.1 \
                          --build-arg EXPOSED_PORT=8081 \
                          -t athul9thd/spring-petclinic-customers-service:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-customers-service:$IMAGE_TAG
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-vets-service/target/spring-petclinic-vets-service-4.0.1 \
                          --build-arg EXPOSED_PORT=8083 \
                          -t athul9thd/spring-petclinic-vets-service:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-vets-service:$IMAGE_TAG
                    '''

                    sh '''
                        docker build -f docker/Dockerfile \
                          --build-arg ARTIFACT_NAME=spring-petclinic-visits-service/target/spring-petclinic-visits-service-4.0.1 \
                          --build-arg EXPOSED_PORT=8082 \
                          -t athul9thd/spring-petclinic-visits-service:$IMAGE_TAG .
                        docker push athul9thd/spring-petclinic-visits-service:$IMAGE_TAG
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
