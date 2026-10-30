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
        githubPush()
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

        stage('Deploy') {
            steps {
                sh '''
                    export DOCKER_IMAGE=$DOCKER_IMAGE
                    export IMAGE_TAG=$IMAGE_TAG
                    docker compose up -d --remove-orphans
                    docker compose ps
                '''
            }
        }

        stage('Verify Health') {
            steps {
                sh '''
                    for i in $(seq 1 20); do
                        if curl -fs http://localhost:8085/actuator/health | grep -q '"status":"UP"'; then
                            echo "Deployment healthy after ${i} attempt(s)"
                            curl -s "http://localhost:8085/api/v1/add?a=2&b=3"
                            exit 0
                        fi
                        echo "Waiting for service... (${i}/20)"
                        sleep 5
                    done
                    echo "Health check FAILED"
                    docker compose logs --tail=60
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Build succeeded.

Job:     ${env.JOB_NAME}
Build:   #${env.BUILD_NUMBER}
Image:   ${DOCKER_IMAGE}:${IMAGE_TAG}
URL:     ${env.BUILD_URL}

The stack is deployed and healthy at http://localhost:8085
"""
            )
        }
        failure {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Build failed.

Job:    ${env.JOB_NAME}
Build:  #${env.BUILD_NUMBER}
URL:    ${env.BUILD_URL}
Console: ${env.BUILD_URL}console
"""
            )
        }
        always {
            sh 'docker image prune -f || true'
        }
    }
}