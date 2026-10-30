pipeline {
    agent any

    environment {
        DOCKER_IMAGE  = 'puskara/calculator-devops'
        IMAGE_TAG     = "${env.BUILD_NUMBER}"
        REGISTRY_CRED = 'dockerhub-creds'
        NOTIFY_EMAIL  = 'pushkar.kulkarni.2005@gmail.com'
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

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t $DOCKER_IMAGE:$IMAGE_TAG \
                      -t $DOCKER_IMAGE:latest .
                    docker images | grep calculator-devops
                '''
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${REGISTRY_CRED}",
                    usernameVariable: 'DH_USER',
                    passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push $DOCKER_IMAGE:$IMAGE_TAG
                        docker push $DOCKER_IMAGE:latest
                        docker logout
                    '''
                }
            }
        }
    }
}