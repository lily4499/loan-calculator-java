pipeline {
    agent any

    environment {
        EC2_IP = "54.162.92.114"
        APP_NAME = "loanapp"
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/lily4499/loan-calculator-java.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
                sh '''
                    for f in target/*.war; do
                        if [ "$f" != "target/loan-calculator.war" ]; then
                            mv "$f" target/loan-calculator.war
                        fi
                    done
                '''
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
                                    sourceFiles: 'target/loan-calculator.war',
                                    removePrefix: 'target',
                                    remoteDirectory: '/opt/tomcat/webapps/',
                                    execCommand: '''
                                        /opt/tomcat/bin/shutdown.sh || true
                                        sleep 5
                                        /opt/tomcat/bin/startup.sh
                                        echo "Deployed loan-calculator.war"
                                    '''
                                )
                            ],
                            verbose: true
                        )
                    ]
                )
            }
        }

        stage('Health Check') {
            steps {
                script {
                    def retries = 5
                    def response = ""
                    for (int i = 0; i < retries; i++) {
                        response = sh(
                            script: "curl -s -o /dev/null -w '%{http_code}' http://${EC2_IP}:8080/loan-calculator/",
                            returnStdout: true
                        ).trim()
                        if (response == "200") {
                            echo "App is up and running! ✅"
                            break
                        } else {
                            echo "Waiting for app... (Attempt ${i + 1}/5) HTTP: ${response}"
                            sleep 5
                        }
                    }
                    if (response != "200") {
                        error "App not reachable. Final HTTP status: ${response}"
                    }
                }
            }
        }

        stage('Notify via Slack') {
            steps {
                slackSend(
                    channel: '#devops-project',
                    message: "✅ Build & deployment of ${APP_NAME} completed successfully.",
                    color: '#36a64f'
                )
            }
        }
    }

    post {
        failure {
            slackSend(
                channel: '#devops-project',
                message: "❌ Build or deployment of ${APP_NAME} failed!",
                color: '#FF0000'
            )
        }
    }
}
