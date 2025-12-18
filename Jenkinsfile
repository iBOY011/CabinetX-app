pipeline {
    agent any

    tools {
        jdk 'jdk17'   // The name you set in Global Tool Configuration
        maven 'Maven3' // If Maven installed via Jenkins, optional
    }

    environment {
        SONAR_TOKEN = credentials('sonar-cabinetx-token')
        MAVEN_OPTS = "-Xms128m -Xmx256m -XX:+UseSerialGC"
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Dependency Check - patient-service') {
            steps {
                dir('patient-service') {
                    sh './mvnw clean install org.owasp:dependency-check-maven:check -DupdateOnly=true'
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
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
