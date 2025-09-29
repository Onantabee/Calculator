pipeline {
    agent { label 'pipeline' }
    environment {
        DOCKER_CONFIG = "${WORKSPACE}/.docker"
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

        stage("Static code analysis") {
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
                withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                    mkdir -p $DOCKER_CONFIG
                    echo '{"credsStore":""}' > $DOCKER_CONFIG/config.json
                    echo $DOCKER_PASS | docker --config $DOCKER_CONFIG login -u $DOCKER_USER --password-stdin
                    docker --config $DOCKER_CONFIG build -t onantabee/calculator .
                    docker --config $DOCKER_CONFIG push onantabee/calculator
                    '''
                }
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