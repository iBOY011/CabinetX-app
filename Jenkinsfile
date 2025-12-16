pipeline {
    agent any

    environment {
        // Token Sonar stocké dans Jenkins (Kind: Secret text ou Username+password pour sonar-scanner)
        SONAR_TOKEN = credentials('sonar-cabinetx-token')
    }

    stages {
        stage('Dependency Check') {
            steps {
                sh '''
                    export MAVEN_OPTS="-Xmx600m -Xms512m"
                    mvn clean install org.owasp:dependency-check-maven:check -DupdateOnly=true
                '''
            }
        }
        
        stage('Build & Test - patient-service') {
            steps {
                dir('patient-service') {
                    sh './mvnw clean verify '
                }
            }
        }

        stage('SonarQube - patient-service') {
            steps {
                dir('patient-service') {
                    sh """
                    ./mvnw sonar:sonar \
                      -Dsonar.projectKey=cabinetx-patient-service \
                      -Dsonar.projectName='CabinetX - patient-service' \
                      -Dsonar.host.url=http://host.docker.internal:9000 \
                      -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }

        // TODO: plus tard vous ajouterez :
        // stage('Build & Test - appointment-service') { ... }
        // stage('SonarQube - appointment-service') { ... }
        // etc.
    }
}
