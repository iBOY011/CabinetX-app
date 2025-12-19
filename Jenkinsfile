pipeline {
    agent any
    
    environment {
        SONAR_TOKEN = credentials('sonar-token')
        // Optimisé pour serveur 4GB avec SonarQube
        MAVEN_OPTS = "-Xms64m -Xmx384m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m -Djava.awt.headless=true"
        MAVEN_CONFIG = "-Dmaven.repo.local=.m2/repository"
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '2'))
        disableConcurrentBuilds()
        timeout(time: 90, unit: 'MINUTES')
        skipDefaultCheckout()
        timestamps()
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Verify Environment') {
            steps {
                sh '''
                    echo "Java version:"
                    java -version
                    echo "Available Memory:"
                    free -h
                    echo "Branch: ${GIT_BRANCH}"
                '''
            }
        }
        
        // BUILD EN SÉQUENTIEL PAR GROUPES - CRITIQUE POUR 4GB RAM
        stage('Build Group 1 - Core Services') {
            steps {
                script {
                    def services = ['discovery-service']
                    services.each { service ->
                        dir(service) {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true \
                                    -DskipTests=false \
                                    -T 1C \
                                    --batch-mode \
                                    --no-transfer-progress
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Build Group 2 - Auth & User') {
            steps {
                script {
                    def services = ['auth-service', 'user-service', 'patient-service']
                    services.each { service ->
                        dir(service) {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true \
                                    -DskipTests=false \
                                    -T 1C \
                                    --batch-mode \
                                    --no-transfer-progress
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Build Group 3 - Clinical Services') {
            steps {
                script {
                    def services = [
                        'appointment-service',
                        'consultation-service',
                        'medical-record-service',
                        'prescription-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true \
                                    -DskipTests=false \
                                    -T 1C \
                                    --batch-mode \
                                    --no-transfer-progress
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Build Group 4 - Business Services') {
            steps {
                script {
                    def services = [
                        'billing-service',
                        'payment-service',
                        'medication-service',
                        'queue-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true \
                                    -DskipTests=false \
                                    -T 1C \
                                    --batch-mode \
                                    --no-transfer-progress
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Build Group 5 - Support Services') {
            steps {
                script {
                    def services = [
                        'clinic-service',
                        'notification-service',
                        'analytics-service',
                        'chatbot-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true \
                                    -DskipTests=false \
                                    -T 1C \
                                    --batch-mode \
                                    --no-transfer-progress
                            '''
                        }
                    }
                }
            }
        }
        
        // SONARQUBE EN SÉQUENTIEL - 2 à la fois maximum
        stage('SonarQube Analysis - Batch 1') {
            steps {
                script {
                    def services = [
                        'discovery-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh """
                                chmod +x mvnw
                                export SONAR_SCANNER_OPTS="-Xmx512m"
                                ./mvnw sonar:sonar \
                                  -Dsonar.projectKey=cabinetx-${service} \
                                  -Dsonar.projectName="${service.replaceAll('-', ' ').capitalize()}" \
                                  -Dsonar.host.url=http://localhost:9000 \
                                  -Dsonar.token=$SONAR_TOKEN \
                                  --batch-mode \
                                  --no-transfer-progress
                            """
                        }
                    }
                }
            }
        }
        
        stage('SonarQube Analysis - Batch 2') {
            steps {
                script {
                    def services = [
                        'auth-service',
                        'user-service',
                        'patient-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh """
                                chmod +x mvnw
                                export SONAR_SCANNER_OPTS="-Xmx512m"
                                ./mvnw sonar:sonar \
                                  -Dsonar.projectKey=cabinetx-${service} \
                                  -Dsonar.projectName="${service.replaceAll('-', ' ').capitalize()}" \
                                  -Dsonar.host.url=http://localhost:9000 \
                                  -Dsonar.token=$SONAR_TOKEN \
                                  --batch-mode \
                                  --no-transfer-progress
                            """
                        }
                    }
                }
            }
        }
        
        stage('SonarQube Analysis - Batch 3') {
            steps {
                script {
                    def services = [
                        'appointment-service',
                        'consultation-service',
                        'medical-record-service',
                        'prescription-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh """
                                chmod +x mvnw
                                export SONAR_SCANNER_OPTS="-Xmx512m"
                                ./mvnw sonar:sonar \
                                  -Dsonar.projectKey=cabinetx-${service} \
                                  -Dsonar.projectName="${service.replaceAll('-', ' ').capitalize()}" \
                                  -Dsonar.host.url=http://localhost:9000 \
                                  -Dsonar.token=$SONAR_TOKEN \
                                  --batch-mode \
                                  --no-transfer-progress
                            """
                        }
                    }
                }
            }
        }
        
        stage('SonarQube Analysis - Batch 4') {
            steps {
                script {
                    def services = [
                        'billing-service',
                        'payment-service',
                        'medication-service',
                        'queue-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh """
                                chmod +x mvnw
                                export SONAR_SCANNER_OPTS="-Xmx512m"
                                ./mvnw sonar:sonar \
                                  -Dsonar.projectKey=cabinetx-${service} \
                                  -Dsonar.projectName="${service.replaceAll('-', ' ').capitalize()}" \
                                  -Dsonar.host.url=http://localhost:9000 \
                                  -Dsonar.token=$SONAR_TOKEN \
                                  --batch-mode \
                                  --no-transfer-progress
                            """
                        }
                    }
                }
            }
        }
        
        stage('SonarQube Analysis - Batch 5') {
            steps {
                script {
                    def services = [
                        'clinic-service',
                        'notification-service',
                        'analytics-service',
                        'chatbot-service'
                    ]
                    services.each { service ->
                        dir(service) {
                            sh """
                                chmod +x mvnw
                                export SONAR_SCANNER_OPTS="-Xmx512m"
                                ./mvnw sonar:sonar \
                                  -Dsonar.projectKey=cabinetx-${service} \
                                  -Dsonar.projectName="${service.replaceAll('-', ' ').capitalize()}" \
                                  -Dsonar.host.url=http://localhost:9000 \
                                  -Dsonar.token=$SONAR_TOKEN \
                                  --batch-mode \
                                  --no-transfer-progress
                            """
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            script {
                // Collecter les résultats de test
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                
                // Nettoyer le workspace pour libérer de l'espace
                cleanWs(
                    deleteDirs: true,
                    patterns: [
                        [pattern: '**/target', type: 'INCLUDE'],
                        [pattern: '**/.m2/repository', type: 'INCLUDE']
                    ]
                )
            }
        }
        success {
            echo 'All 16 services built and analyzed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
        unstable {
            echo 'Pipeline completed with warnings.'
        }
    }
}