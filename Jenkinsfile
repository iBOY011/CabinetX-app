pipeline {
    agent any
    environment {
        SONAR_TOKEN = credentials('sonar-cabinetx-token')
        MAVEN_OPTS = "-Xms128m -Xmx256m -XX:+UseSerialGC"
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {
        stage('Verify Environment') {
            steps {
                sh '''
                    echo "Java version:"
                    java -version
                    echo "JAVA_HOME: $JAVA_HOME"
                    echo "PATH: $PATH"
                    echo "Branch: ${GIT_BRANCH}"
                '''
            }
        }

        stage('Build & Test - All Services') {
            parallel {
                stage('discovery-service') {
                    steps {
                        dir('discovery-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('gateway-service') {
                    steps {
                        dir('gateway-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('patient-service') {
                    steps {
                        dir('patient-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('user-service') {
                    steps {
                        dir('user-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('auth-service') {
                    steps {
                        dir('auth-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('appointment-service') {
                    steps {
                        dir('appointment-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('billing-service') {
                    steps {
                        dir('billing-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('clinic-service') {
                    steps {
                        dir('clinic-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('consultation-service') {
                    steps {
                        dir('consultation-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('medical-record-service') {
                    steps {
                        dir('medical-record-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('medication-service') {
                    steps {
                        dir('medication-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('notification-service') {
                    steps {
                        dir('notification-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('payment-service') {
                    steps {
                        dir('payment-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('prescription-service') {
                    steps {
                        dir('prescription-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('queue-service') {
                    steps {
                        dir('queue-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('analytics-service') {
                    steps {
                        dir('analytics-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
                stage('chatbot-service') {
                    steps {
                        dir('chatbot-service') {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify -Ddependency-check.skip=true
                            '''
                        }
                    }
                }
            }
        }

        stage('SonarCloud Analysis') {
            parallel {
                stage('sonar-patient-service') {
                    steps {
                        dir('patient-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-patient-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-user-service') {
                    steps {
                        dir('user-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-user-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-auth-service') {
                    steps {
                        dir('auth-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-auth-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-appointment-service') {
                    steps {
                        dir('appointment-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-appointment-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-billing-service') {
                    steps {
                        dir('billing-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-billing-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-clinic-service') {
                    steps {
                        dir('clinic-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-clinic-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-consultation-service') {
                    steps {
                        dir('consultation-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-consultation-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
                stage('sonar-medical-record-service') {
                    steps {
                        dir('medical-record-service') {
                            sh """
                            chmod +x mvnw
                            ./mvnw sonar:sonar \
                              -Dsonar.organization=cabinet \
                              -Dsonar.projectKey=cabinetx-medical-record-service \
                              -Dsonar.host.url=https://sonarcloud.io \
                              -Dsonar.login=${SONAR_TOKEN} \
                              -Dsonar.branch.name=${GIT_BRANCH}
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'All services built and tested successfully!'
        }
        failure {
            echo 'One or more services failed to build or test.'
        }
    }
}