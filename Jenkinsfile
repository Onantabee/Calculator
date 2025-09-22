pipeline {
    agent { label 'pipeline' }

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
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'build/reports/jacoco/test/html',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Report'
                ])
                sh "./gradlew jacocoTestCoverageVerification"
            }
        }

        stage("Static code analysis") {
            steps {
                sh "./gradlew checkstyleMain"
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'build/reports/checkstyle',
                    reportFiles: 'main.html',
                    reportName: 'Checkstyle Report'
                ])
            }
        }

        stage('Build') {
                    steps {
                        sh "./gradlew build"
                        withCredentials([usernamePassword(
                            credentialsId: 'docker-hub-credentials',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {
                            sh '''
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            '''
                            sh "docker build -t onantabee/calculator:latest ."
                        }
                    }
                }

                stage('Deploy') {
                    steps {
                        withCredentials([usernamePassword(
                            credentialsId: 'docker-hub-credentials',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {
                            sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                            sh "docker push onantabee/calculator:latest"
                        }
                    }
    }

    post {
        always {
            mail to: 'onantab47@gmail.com',
                subject: "Completed Pipeline: ${currentBuild.fullDisplayName}",
                body: "Your build completed, please check: ${env.BUILD_URL}"
            slackSend channel: '#oma-test-channel',
                     color: currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red',
                     message: "Pipeline ${currentBuild.fullDisplayName} completed with status: ${currentBuild.currentResult}"
        }
    }
}