pipeline {
    agent any

    tools {
        jdk 'jdk-17'
        maven 'maven-3.8.7'
    }

    environment {
        DOCKERHUB_CREDS = credentials('dockerhub-creds')
    }

    stages {

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                sh '''
                  mvn clean install -DskipTests
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                      mvn verify -DskipTests \
                      -Dsonar.projectKey=spring-petclinic-microservices \
                      -Dsonar.projectName=spring-petclinic-microservices \
                      -Dsonar.host.url=$SONAR_HOST_URL \
                      -Dsonar.login=$SONAR_AUTH_TOKEN
                    '''
                }
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                sh '''
                  echo "$DOCKERHUB_CREDS_PSW" | docker login -u "$DOCKERHUB_CREDS_USR" --password-stdin

                  chmod +x scripts/*.sh

                  ./scripts/run_all.sh
                  ./scripts/tagImages.sh
                  ./scripts/pushImages.sh
                '''
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline completed successfully'
        }

        failure {
            echo 'CI Pipeline failed'
        }

        always {
            cleanWs()
        }
    }
}
