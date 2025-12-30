pipeline {
    agent any

    tools {
        maven 'maven-3.8.7'
        jdk 'jdk-17'
    }

    parameters {
        choice(name: 'ENV', choices: ['dev', 'uat', 'prod'], description: 'Target environment')
        string(name: 'IMAGE_TAG', defaultValue: 'dev', description: 'Docker image tag')
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Deploy to Kubernetes')
    }

    environment {
        DOCKERHUB_USERNAME = 'athul9thd'
        K8S_BASE = 'k8s/petclinic'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Athul675/spring-petclinic-microservices.git',
                    credentialsId: 'github-pat'
            }
        }

        stage('Build, Sonar & Docker (Per Service)') {
            steps {
                script {

                    def services = [
                      "spring-petclinic-config-server",
                      "spring-petclinic-discovery-server",
                      "spring-petclinic-admin-server",
                      "spring-petclinic-customers-service",
                      "spring-petclinic-vets-service",
                      "spring-petclinic-visits-service",
                      "spring-petclinic-api-gateway",
                      "spring-petclinic-genai-service"
                    ]

                    withCredentials([
                        usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS'),
                        string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')
                    ]) {

                        sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                        for (svc in services) {

                            stage("Build ${svc}") {
                                dir(svc) {
                                    sh 'mvn clean package -DskipTests'
                                }
                            }

                            stage("Sonar ${svc}") {
                                dir(svc) {
                                    withSonarQubeEnv('sonarqube') {
                                        sh """
                                        /opt/sonar-scanner/bin/sonar-scanner \
                                          -Dsonar.login=$SONAR_TOKEN
                                        """
                                    }
                                }
                            }

                            stage("Docker ${svc}") {
                                dir(svc) {
                                    sh """
                                    docker build -t ${DOCKERHUB_USERNAME}/${svc}:${IMAGE_TAG} .
                                    docker push ${DOCKERHUB_USERNAME}/${svc}:${IMAGE_TAG}
                                    """
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Kubernetes Deploy') {
            when {
                expression { params.DEPLOY }
            }
            steps {
                sh """
                kubectl apply -f ${K8S_BASE}/namespace
                kubectl apply -f ${K8S_BASE}/database
                kubectl apply -f ${K8S_BASE}/config-server
                kubectl apply -f ${K8S_BASE}/discovery
                kubectl apply -f ${K8S_BASE}/admin-server

                kubectl apply -f ${K8S_BASE}/micro-services/customers
                kubectl apply -f ${K8S_BASE}/micro-services/vets
                kubectl apply -f ${K8S_BASE}/micro-services/visits
                kubectl apply -f ${K8S_BASE}/micro-services/api-gateway

                kubectl apply -f ${K8S_BASE}/ingress
                kubectl apply -f ${K8S_BASE}/hpa
                """
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
