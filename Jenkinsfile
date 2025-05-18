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

        stage('Deploy to EC2 Tomcat') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: 'tomcat-ec2',  // defined in Jenkins > Manage Jenkins > Configure System
                            transfers: [
                                sshTransfer(
                                    sourceFiles: 'target/loan-calculator.war',
                                    removePrefix: '',
                                    remoteDirectory: '/opt/tomcat/webapps',
                                    execCommand: '''
                                        echo "[Before Restart] Deployed apps:"
                                        ls -l /opt/tomcat/webapps
                                        echo "[Stopping Tomcat]"
                                        /opt/tomcat/bin/shutdown.sh || true
                                        sleep 5
                                        echo "[Starting Tomcat]"
                                        /opt/tomcat/bin/startup.sh
                                        echo "[After Restart] Deployed apps:"
                                        ls -l /opt/tomcat/webapps
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
                    def success = false

                    for (int i = 0; i < retries; i++) {
                        response = sh(
                            script: "curl -s -o /dev/null -w '%{http_code}' http://${EC2_IP}:8080/loan-calculator/",
                            returnStdout: true
                        ).trim()

                        if (response == "200") {
                            echo "✅ App is up and reachable!"
                            success = true
                            break
                        } else {
                            echo "Waiting for app... HTTP status: ${response} (Retry ${i + 1}/5)"
                            sleep 5
                        }
                    }

                    if (!success) {
                        error "❌ Health check failed. Final HTTP status: ${response}"
                    }
                }
            }
        }

        stage('Notify via Slack') {
            steps {
                slackSend(
                    channel: '#devops-project',
                    message: "✅ ${APP_NAME} deployed to EC2 Tomcat successfully.",
                    color: '#36a64f'
                )
            }
        }
    }

    post {
        failure {
            slackSend(
                channel: '#devops-project',
                message: "❌ ${APP_NAME} build or deployment failed.",
                color: '#FF0000'
            )
        }
    }
}
