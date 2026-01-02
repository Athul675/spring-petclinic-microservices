pipeline {
    agent any
    tools {
        maven 'maven-3.8.7'
        jdk 'jdk-17'
    }
    parameters {
        choice(name: 'ENV', choices: ['dev', 'uat', 'prod'], description: 'Target environment')
        string(name: 'IMAGE_TAG', defaultValue: 'latest', description: 'Docker image tag')
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Deploy to Kubernetes')
    }
    environment {
        DOCKERHUB_USERNAME = 'athul9thd'
        K8S_BASE = 'k8s/petclinic'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Athul675/spring-petclinic-microservices.git', credentialsId: 'github-pat'
            }
        }
        stage('Process Microservices') {
            steps {
                script {
                    // Added genai-service to the list
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
                    for (svc in services) {
                        dir(svc) {
                            echo "--- Processing ${svc} ---"
                            sh "mvn clean package -DskipTests"
                            
                            // SonarQube Analysis
                            withSonarQubeEnv('sonarqube') {
                                sh "/opt/sonar-scanner/bin/sonar-scanner"
                            }
                            
                            // Docker Build and Push
                            withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'U', passwordVariable: 'P')]) {
                                sh "docker build -t ${DOCKERHUB_USERNAME}/${svc}:${IMAGE_TAG} ."
                                sh "echo $P | docker login -u $U --password-stdin"
                                sh "docker push ${DOCKERHUB_USERNAME}/${svc}:${IMAGE_TAG}"
                            }
                        }
                    }
                }
            }
        }
        stage('Kubernetes Deploy') {
            when { expression { params.DEPLOY } }
            steps {
                script {
                    echo "--- Applying K8s Manifests ---"
                    // 1. Namespace
                    sh "kubectl apply -f ${K8S_BASE}/namespace"
                    
                    // 2. Database (Secrets & StatefulSets)
                    sh "kubectl apply -f ${K8S_BASE}/database"
                    
                    // 3. Infrastructure Services
                    sh "kubectl apply -f ${K8S_BASE}/config-server"
                    sh "kubectl apply -f ${K8S_BASE}/discovery"
                    sh "kubectl apply -f ${K8S_BASE}/admin-server"
                    
                    // 4. Business Microservices (Added genai-service path if you have one, 
                    // otherwise it follows the pattern below)
                    sh "kubectl apply -f ${K8S_BASE}/micro-services/customers"
                    sh "kubectl apply -f ${K8S_BASE}/micro-services/vets"
                    sh "kubectl apply -f ${K8S_BASE}/micro-services/visits"
                    sh "kubectl apply -f ${K8S_BASE}/micro-services/api-gateway"
                    
                    // 5. Apply missing HPA and Ingress
                    echo "--- Applying HPA and Ingress ---"
                    sh "kubectl apply -f ${K8S_BASE}/hpa"
                    sh "kubectl apply -f ${K8S_BASE}/ingress"
                    
                    // 6. Optional: Force rollout if using 'latest' tag to ensure pods update
                    if (params.IMAGE_TAG == 'latest') {
                        sh "kubectl rollout restart deployment -n petclinic"
                    }
                }
            }
        }
    }
    post { always { cleanWs() } }
}
