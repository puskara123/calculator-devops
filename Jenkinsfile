pipeline {
    agent any

    environment {
        DOCKER_IMAGE  = 'DOCKERHUB_USER/calculator-devops'
        IMAGE_TAG     = "${env.BUILD_NUMBER}"
        REGISTRY_CRED = 'dockerhub-creds'
        NOTIFY_EMAIL  = 'you@iiitb.ac.in'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 20, unit: 'MINUTES')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --pretty=format:"%h %s"'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}