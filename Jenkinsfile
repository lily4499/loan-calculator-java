pipeline {
    agent any

    environment {
        EC2_IP = "54.162.92.114"
        APP_NAME = "loanapp"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/lili4499/loan-calculator-java.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Deploy to Tomcat on EC2') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: 'tomcat-ec2',
                            transfers: [
                                sshTransfer(
                                    sourceFiles: '**/target/*.war',
                                    remoteDirectory: '/opt/tomcat/webapps/',
                                    removePrefix: 'target',
                                    execCommand: '/opt/tomcat/bin/shutdown.sh; sleep 5; /opt/tomcat/bin/startup.sh'
                                )
                            ],
                            usePromotionTimestamp: false,
                            verbose: true
                        )
                    ]
                )
            }
        }

        stage('Notify via Slack') {
            steps {
                slackSend(channel: '#ci-cd', message: "Build & deployment of ${APP_NAME} completed ✅", color: '#36a64f')
            }
        }
    }

    post {
        failure {
            slackSend(channel: '#ci-cd', message: "Build failed for ${APP_NAME} ❌", color: '#FF0000')
        }
    }
}
