pipeline {
    agent any

    environment {
        // Token Sonar stocké dans Jenkins (Kind: Secret text)
        SONAR_TOKEN = credentials('sonar-cabinetx-token')
        MAVEN_OPTS = "-Xms128m -Xmx256m -XX:+UseSerialGC"
    }

    options {
        disableConcurrentBuilds() // Avoid multiple jobs consuming RAM
        timeout(time: 30, unit: 'MINUTES') // Prevent stuck jobs
    }

    stages {
        stage('Dependency Check - patient-service') {
            steps {
                dir('patient-service') {
                    sh '''
                        ./mvnw clean install org.owasp:dependency-check-maven:check -DupdateOnly=true
                    '''
                }
            }
        }

        stage('Build & Test - patient-service') {
            steps {
                dir('patient-service') {
                    sh './mvnw clean verify'
                }
            }
        }

        stage('SonarCloud Analysis - patient-service') {
            steps {
                dir('patient-service') {
                    sh """
                    ./mvnw sonar:sonar \
                    -Dsonar.projectKey=cabinetx-patient-service \
                    -Dsonar.host.url=https://sonarcloud.io \
                    -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        // Future microservices:
        // stage('Build & Test - appointment-service') { ... }
        // stage('SonarCloud - appointment-service') { ... }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
