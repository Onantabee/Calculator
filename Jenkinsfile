pipeline {
    agent { label 'pipeline' }

    environment {
        WORKSPACE_DOCKER_CONFIG = "${WORKSPACE}/.docker"
    }

    stages {
        stage('Checkout') {
            steps {
                git url: "https://github.com/Onantabee/Calculator.git", branch: "master"
            }
        }

        stage('Linux Permission') {
            steps {
                sh "chmod +x gradlew"
                sh "docker version"
            }
        }

        stage('Unit Test') {
            steps {
                sh "./gradlew test"
            }
        }

        stage('Code Coverage') {
            steps {
                sh "./gradlew jacocoTestReport"
                publishHTML(target: [
                    reportDir: 'build/reports/jacoco/test/html',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Report'
                ])
                sh "./gradlew jacocoTestCoverageVerification"
            }
        }

        stage("Static Code Analysis") {
            steps {
                sh "./gradlew checkstyleMain"
                publishHTML(target: [
                    reportDir: 'build/reports/checkstyle',
                    reportFiles: 'main.html',
                    reportName: 'Checkstyle Report'
                ])
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([string(credentialsId: 'docker-hub', variable: 'DOCKER_TOKEN')]) {
                    sh """
                        # Create isolated Docker config
                        mkdir -p ${WORKSPACE_DOCKER_CONFIG}
                        echo '{ "credsStore": "" }' > ${WORKSPACE_DOCKER_CONFIG}/config.json

                        # Login to Docker Hub using token (no Keychain)
                        echo \$DOCKER_TOKEN | docker --config ${WORKSPACE_DOCKER_CONFIG} login -u onantabee --password-stdin

                        # Build and push Docker image
                        docker --config ${WORKSPACE_DOCKER_CONFIG} build -t onantabee/calculator .
                        docker --config ${WORKSPACE_DOCKER_CONFIG} push onantabee/calculator
                    """
                }
            }
        }
    }

    post {
        always {
            // Email notification
            mail to: 'onantab47@gmail.com',
                 subject: "Pipeline Completed: ${currentBuild.fullDisplayName}",
                 body: "Your build has completed. Check details here: ${env.BUILD_URL}"

            // Slack notification
            slackSend channel: '#oma-test-channel',
                      color: currentBuild.currentResult == 'SUCCESS' ? 'good' : 'danger',
                      message: "Pipeline ${currentBuild.fullDisplayName} completed with status: ${currentBuild.currentResult}"
        }
    }
}