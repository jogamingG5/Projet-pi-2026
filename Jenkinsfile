pipeline {
    agent any
    
    environment {
        REGISTRY = 'ghcr.io/daly-belguith'
        SONAR_HOST = 'http://sonarqube:9000'
        SONAR_TOKEN = credentials('sonarqube-token')
        GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
        IMAGE_TAG = "${GIT_COMMIT_SHORT}-${BUILD_NUMBER}"
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    echo "✓ Repository checked out: ${GIT_COMMIT_SHORT}"
                }
            }
        }
        
        stage('Backend: Maven Build') {
            steps {
                dir('back') {
                    script {
                        echo "🔨 Building backend with Maven..."
                        sh '''
                            mvn clean
                            mvn -B install -DskipTests
                            mvn -B package -DskipTests
                        '''
                    }
                }
            }
        }
        
        stage('Backend: Unit Tests') {
            steps {
                dir('back') {
                    script {
                        echo "🧪 Running backend tests..."
                        sh 'mvn -B test'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'back/target/surefire-reports/**/*.xml'
                }
            }
        }
        
        stage('Backend: SonarQube Scan') {
            steps {
                dir('back') {
                    script {
                        echo "📊 SonarQube analysis..."
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=streetleague-backend \
                              -Dsonar.sources=src/main/java \
                              -Dsonar.tests=src/test/java \
                              -Dsonar.host.url=${SONAR_HOST} \
                              -Dsonar.login=${SONAR_TOKEN}
                        '''
                    }
                }
            }
        }
        
        stage('Frontend: Install & Build') {
            steps {
                dir('front/streetleague') {
                    script {
                        echo "🔨 Building frontend..."
                        sh '''
                            npm ci
                            npm run build
                        '''
                    }
                }
            }
        }
        
        stage('Frontend: Unit Tests') {
            steps {
                dir('front/streetleague') {
                    script {
                        echo "🧪 Running frontend tests..."
                        sh 'npm run test -- --watch=false' || true
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'front/streetleague/test-results/*.xml'
                }
            }
        }
        
        stage('Docker: Build & Push Backend') {
            steps {
                script {
                    echo "🐳 Building backend Docker image..."
                    sh '''
                        cd back
                        docker build -t ${REGISTRY}/streetleague-backend:${IMAGE_TAG} .
                        docker tag ${REGISTRY}/streetleague-backend:${IMAGE_TAG} ${REGISTRY}/streetleague-backend:latest
                        echo "${DOCKER_HUB_PASSWORD}" | docker login ghcr.io -u ${DOCKER_HUB_USERNAME} --password-stdin
                        docker push ${REGISTRY}/streetleague-backend:${IMAGE_TAG}
                        docker push ${REGISTRY}/streetleague-backend:latest
                    '''
                }
            }
        }
        
        stage('Docker: Build & Push Frontend') {
            steps {
                script {
                    echo "🐳 Building frontend Docker image..."
                    sh '''
                        cd front
                        docker build -t ${REGISTRY}/streetleague-frontend:${IMAGE_TAG} .
                        docker tag ${REGISTRY}/streetleague-frontend:${IMAGE_TAG} ${REGISTRY}/streetleague-frontend:latest
                        echo "${DOCKER_HUB_PASSWORD}" | docker login ghcr.io -u ${DOCKER_HUB_USERNAME} --password-stdin
                        docker push ${REGISTRY}/streetleague-frontend:${IMAGE_TAG}
                        docker push ${REGISTRY}/streetleague-frontend:latest
                    '''
                }
            }
        }
        
        stage('Deploy: Update K8s Manifests') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "🚀 Updating K8s manifests with image tag ${IMAGE_TAG}..."
                    sh '''
                        sed -i "s|ghcr.io/daly-belguith/streetleague-backend:.*|${REGISTRY}/streetleague-backend:${IMAGE_TAG}|g" k8s/deployment.yaml
                        sed -i "s|ghcr.io/daly-belguith/streetleague-frontend:.*|${REGISTRY}/streetleague-frontend:${IMAGE_TAG}|g" k8s/deployment.yaml
                        cat k8s/deployment.yaml
                    '''
                }
            }
        }
        
        stage('Deploy: Apply K8s Manifests') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "☸️  Deploying to Kubernetes..."
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh '''
                            export KUBECONFIG=${KUBECONFIG}
                            kubectl apply -f k8s/config.yaml
                            kubectl apply -f k8s/deployment.yaml
                            kubectl rollout status deployment/streetleague-backend -n streetleague --timeout=5m
                            kubectl rollout status deployment/streetleague-frontend -n streetleague --timeout=5m
                        '''
                    }
                }
            }
        }
        
        stage('Verify Deployment') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "✅ Verifying deployment..."
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh '''
                            export KUBECONFIG=${KUBECONFIG}
                            kubectl get pods -n streetleague
                            kubectl get svc -n streetleague
                        '''
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo "✅ Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed!"
        }
    }
}
