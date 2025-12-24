pipeline {
    agent any

    tools {
        maven 'maven-3.8.7'
        jdk 'jdk-17'
    }

    environment {
        DOCKERHUB_USERNAME = 'athul9thd'
        DOCKERHUB_CREDENTIALS = 'dockerhub-creds'
        SONARQUBE_SERVER = 'sonarqube'
        SONARQUBE_TOKEN = credentials('sonarqube-token')
        APP_VERSION = '4.0.1'
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Athul675/spring-petclinic-microservices.git',
                        credentialsId: 'github-pat'
                    ]]
                ])
            }
        }

        stage('Maven Build') {
            steps {
                sh '''
                ./mvnw clean package
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                    sonar-scanner \
                      -Dsonar.login=${SONARQUBE_TOKEN}
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {

                    def services = [
                        'spring-petclinic-api-gateway',
                        'spring-petclinic-customers-service',
                        'spring-petclinic-vets-service',
                        'spring-petclinic-visits-service',
                        'spring-petclinic-discovery-server',
                        'spring-petclinic-config-server',
                        'spring-petclinic-admin-server',
                        'spring-petclinic-genai-service'
                    ]

                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {

                        for (service in services) {

                            sh """
                            cd ${service}
                            docker build -t ${DOCKERHUB_USERNAME}/${service}:${APP_VERSION}-${BUILD_NUMBER} .
                            docker push ${DOCKERHUB_USERNAME}/${service}:${APP_VERSION}-${BUILD_NUMBER}
                            cd ..
                            """
                        }
                    }
                }
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
        cleanup {
            cleanWs()
        }
    }
}

