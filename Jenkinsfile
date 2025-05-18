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
                sh 'mv target/*.war target/loan-calculator.war' // Rename WAR to a fixed name
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
                    def response = sh(
                        script: "curl -s -o /dev/null -w '%{http_code}' http://54.162.92.114:8080/loan-calculator/",
                        returnStdout: true
                    ).trim()
                    if (response != "200") {
                        error "App not reachable. Got HTTP ${response}"
                    } else {
                        echo "App deployed successfully! ✅"
                    }
                }
            }
        }

        stage('Notify via Slack') {
            steps {
                slackSend(channel: '#devops-project', message: "Build & deployment of ${APP_NAME} completed ✅", color: '#36a64f')
            }
        }
    }

    post {
        failure {
            slackSend(channel: '#devops-project', message: "Build failed for ${APP_NAME} ❌", color: '#FF0000')
        }
    }
}
