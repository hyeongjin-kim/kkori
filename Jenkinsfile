pipeline {
    agent any
    
    environment {
        // Docker 이미지 정보
        DOCKER_IMAGE = "jin0410/kkori-backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        
        // Jenkins에 저장된 credential ID들
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        
        // RDS 연결 정보 - Jenkins Credentials에서 보안 관리
        DB_HOST = "kkori-mysql-db.cbgecwou8vyh.ap-northeast-2.rds.amazonaws.com"
        DB_PORT = "3306"
        DB_NAME = "kkori"
        DB_USERNAME = "admin"
        DB_PASSWORD = credentials('mysql-password')  // Jenkins Secret 사용
        
        // GMS API 설정 - Jenkins Credentials에서 보안 관리
        GMS_API_KEY = credentials('gms-api-key')  // Jenkins Secret 사용
        GMS_WHISPER_URL = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/audio/transcriptions"
        GMS_GPT_URL = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/chat/completions"
        
        // JWT 설정 - Jenkins Credentials에서 보안 관리
        JWT_SECRET_KEY = credentials('jwt-secret-key')  // Jenkins Secret 사용
        JWT_ACCESS_TOKEN_MINUTE_TIME = "30"
        JWT_REFRESH_TOKEN_MINUTE_TIME = "10080"
        
        // Kakao OAuth2 설정 - Jenkins Credentials에서 보안 관리
        KAKAO_CLIENT_ID = credentials('kakao-client-id')  // Jenkins Secret 사용
        KAKAO_CLIENT_SECRET = credentials('kakao-client-secret')  // Jenkins Secret 사용
        KAKAO_REDIRECT_URL = "https://kkori.site/oauth2/authorization/kakao/callback"
        KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token"
        
        // 배포 환경 설정
        SPRING_PROFILES_ACTIVE = "prod"
        DDL_AUTO = "update"  // 첫 배포: create, 이후: update 또는 validate
    }
    
    stages {
        stage('🏗️ Checkout') {
            steps {
                echo '📥 Checking out source code from GitLab...'
                checkout scm
                
                // Git 정보 출력
                sh '''
                    echo "Current branch: $(git branch --show-current)"
                    echo "Latest commit: $(git log --oneline -1)"
                '''
            }
        }
        
        stage('🔧 Prepare Environment') {
            steps {
                echo '🔧 Preparing build environment...'
                sh '''
                    # Gradle wrapper 실행 권한 부여
                    chmod +x backend/kkori/gradlew
                    
                    # Java 및 Gradle 버전 확인
                    java -version
                    cd backend/kkori && ./gradlew --version
                    
                    # Node.js 및 npm 버전 확인
                    node --version
                    npm --version
                    
                    # 프로젝트 정보 출력
                    echo "Project structure:"
                    ls -la
                '''
            }
        }
        
        stage('🧪 Test') {
            steps {
                echo '🧪 Running tests...'
                dir('backend/kkori') {
                    sh '''
                        # 테스트 실행 (H2 DB 사용)
                        export SPRING_PROFILES_ACTIVE=test
                        ./gradlew clean test --no-daemon --stacktrace
                    '''
                }
            }
            post {
                always {
                    // 테스트 결과 리포트
                    publishTestResults testResultsPattern: 'backend/kkori/build/test-results/test/*.xml'
                    
                    // 테스트 리포트 HTML 게시
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'backend/kkori/build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ])
                }
                failure {
                    echo '❌ Tests failed. Pipeline will be terminated.'
                }
            }
        }
        
        stage('📦 Build JAR') {
            steps {
                echo '📦 Building JAR file...'
                dir('backend/kkori') {
                    sh '''
                        # JAR 파일 빌드
                        ./gradlew bootJar --no-daemon --stacktrace
                        
                        # 빌드 결과 확인
                        echo "Build artifacts:"
                        ls -la build/libs/
                        
                        # JAR 파일 크기 확인
                        du -h build/libs/*.jar
                    '''
                }
            }
        }
        
        stage('🎨 Build Frontend') {
            steps {
                echo '🎨 Building frontend...'
                dir('frontend') {
                    sh '''
                        # Node.js 및 npm 버전 확인
                        node --version
                        npm --version
                        
                        # 의존성 설치
                        npm ci
                        
                        # 프로덕션 빌드
                        npm run build
                        
                        # 빌드 결과 확인
                        echo "Frontend build artifacts:"
                        ls -la dist/
                        
                        # 빌드 크기 확인
                        du -sh dist/
                    '''
                }
            }
        }
        
        stage('🐳 Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                script {
                    try {
                        // Docker 이미지 빌드 (backend 경로 지정)
                        sh "docker build -f backend/kkori/Dockerfile -t ${DOCKER_IMAGE}:${DOCKER_TAG} backend/kkori"
                        sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                        
                        // 이미지 정보 확인
                        sh """
                            echo "Built Docker images:"
                            docker images | grep ${DOCKER_IMAGE}
                            
                            echo "Image size:"
                            docker images ${DOCKER_IMAGE}:${DOCKER_TAG} --format "table {{.Repository}}\\t{{.Tag}}\\t{{.Size}}"
                        """
                    } catch (Exception e) {
                        error "❌ Docker build failed: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('📤 Push to Docker Hub') {
            steps {
                echo '📤 Pushing Docker image to Docker Hub...'
                script {
                    try {
                        // Docker Hub 로그인
                        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                        
                        // 이미지 푸시
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                        
                        echo "✅ Successfully pushed ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        echo "✅ Successfully pushed ${DOCKER_IMAGE}:latest"
                        
                    } catch (Exception e) {
                        error "❌ Docker push failed: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('🚀 Deploy Backend to EC2') {
            steps {
                echo '🚀 Deploying backend application to EC2...'
                script {
                    try {
                        // 기존 컨테이너 중지 및 제거
                        sh '''
                            echo "Stopping existing container..."
                            docker stop kkori-backend || echo "No container to stop"
                            docker rm kkori-backend || echo "No container to remove"
                            
                            echo "Cleaning up unused images..."
                            docker image prune -f
                        '''
                        
                        // 새 컨테이너 실행
                        sh """
                            echo "Starting new container..."
                            docker run -d \\
                                --name kkori-backend \\
                                --restart unless-stopped \\
                                -p 8080:8080 \\
                                -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \\
                                -e DB_HOST=${DB_HOST} \\
                                -e DB_PORT=${DB_PORT} \\
                                -e DB_NAME=${DB_NAME} \\
                                -e DB_USERNAME=${DB_USERNAME} \\
                                -e DB_PASSWORD=${DB_PASSWORD} \\
                                -e DDL_AUTO=${DDL_AUTO} \\
                                -e GMS_API_KEY=${GMS_API_KEY} \\
                                -e GMS_WHISPER_URL=${GMS_WHISPER_URL} \\
                                -e GMS_GPT_URL=${GMS_GPT_URL} \\
                                -e JWT_SECRET_KEY=${JWT_SECRET_KEY} \\
                                -e JWT_ACCESS_TOKEN_MINUTE_TIME=${JWT_ACCESS_TOKEN_MINUTE_TIME} \\
                                -e JWT_REFRESH_TOKEN_MINUTE_TIME=${JWT_REFRESH_TOKEN_MINUTE_TIME} \\
                                -e KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID} \\
                                -e KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET} \\
                                -e KAKAO_REDIRECT_URL=${KAKAO_REDIRECT_URL} \\
                                -e KAKAO_TOKEN_URL=${KAKAO_TOKEN_URL} \\
                                -e TZ=Asia/Seoul \\
                                ${DOCKER_IMAGE}:${DOCKER_TAG}
                        """
                        
                        // 컨테이너 시작 확인
                        sh '''
                            echo "Waiting for container to start..."
                            sleep 10
                            
                            if docker ps | grep -q kkori-backend; then
                                echo "✅ Container is running"
                                docker ps | grep kkori-backend
                            else
                                echo "❌ Container failed to start"
                                docker logs kkori-backend || true
                                exit 1
                            fi
                        '''
                        
                    } catch (Exception e) {
                        error "❌ Deployment failed: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('📦 Deploy Frontend to Nginx') {
            steps {
                echo '📦 Deploying frontend to Nginx...'
                script {
                    try {
                        sh '''
                            # nginx 설정에 맞는 디렉토리 생성
                            sudo mkdir -p /var/www/kkori/frontend
                            
                            # 기존 파일 백업
                            if [ -d "/var/www/kkori/frontend" ] && [ "$(ls -A /var/www/kkori/frontend)" ]; then
                                sudo cp -r /var/www/kkori/frontend /var/www/kkori/frontend.backup.$(date +%Y%m%d_%H%M%S)
                                echo "✅ Backup created"
                            fi
                            
                            # 기존 파일 제거
                            sudo rm -rf /var/www/kkori/frontend/*
                            
                            # 새 빌드 파일 배포 (nginx root와 일치)
                            sudo cp -r frontend/dist/* /var/www/kkori/frontend/
                            
                            # 권한 설정
                            sudo chown -R www-data:www-data /var/www/kkori/frontend
                            sudo chmod -R 755 /var/www/kkori/frontend
                            
                            # 배포 결과 확인
                            echo "Frontend deployment status:"
                            ls -la /var/www/kkori/frontend/
                            
                            # nginx 설정 테스트
                            sudo nginx -t
                            
                            # nginx 리로드
                            sudo systemctl reload nginx
                            
                            echo "✅ Frontend deployed successfully"
                        '''
                    } catch (Exception e) {
                        error "❌ Frontend deployment failed: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('🏥 Health Check') {
            steps {
                echo '🏥 Performing application health check...'
                script {
                    def healthCheckPassed = false
                    def maxRetries = 12  // 최대 2분 대기 (12 * 10초)
                    def retryCount = 0
                    
                    while (retryCount < maxRetries && !healthCheckPassed) {
                        try {
                            sleep(time: 10, unit: 'SECONDS')
                            retryCount++
                            
                            echo "🔍 Health check attempt ${retryCount}/${maxRetries}..."
                            
                            // 컨테이너 상태 확인
                            def containerStatus = sh(
                                script: 'docker inspect --format="{{.State.Status}}" kkori-backend',
                                returnStdout: true
                            ).trim()
                            
                            if (containerStatus != 'running') {
                                echo "⚠️ Container is not running (status: ${containerStatus})"
                                sh 'docker logs --tail 50 kkori-backend || true'
                                continue
                            }
                            
                            // HTTP 헬스체크
                            def healthResponse = sh(
                                script: 'curl -f -s http://localhost:8080/actuator/health',
                                returnStatus: true
                            )
                            
                            if (healthResponse == 0) {
                                healthCheckPassed = true
                                echo "✅ Health check passed!"
                                
                                // 헬스체크 상세 정보 출력
                                sh '''
                                    echo "Application health status:"
                                    curl -s http://localhost:8080/actuator/health | python3 -m json.tool || curl -s http://localhost:8080/actuator/health
                                    
                                    echo "Application info:"
                                    curl -s http://localhost:8080/actuator/info | python3 -m json.tool || curl -s http://localhost:8080/actuator/info
                                '''
                            } else {
                                echo "⚠️ Health check failed, retrying in 10 seconds..."
                                // 컨테이너 로그 출력
                                sh 'docker logs --tail 20 kkori-backend || true'
                            }
                            
                        } catch (Exception e) {
                            echo "⚠️ Health check error: ${e.getMessage()}"
                        }
                    }
                    
                    if (!healthCheckPassed) {
                        // 최종 실패시 로그 수집
                        sh '''
                            echo "❌ Final health check failed. Collecting logs..."
                            echo "=== Container Status ==="
                            docker ps | grep kkori-backend || true
                            
                            echo "=== Container Logs ==="
                            docker logs --tail 100 kkori-backend || true
                            
                            echo "=== System Resources ==="
                            free -h
                            df -h
                        '''
                        error "❌ Application health check failed after ${maxRetries} attempts"
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up...'
            script {
                try {
                    // Docker 로그아웃
                    sh 'docker logout || true'
                    
                    // 불필요한 이미지 정리 (빌드 캐시 제외)
                    sh 'docker system prune -f || true'
                    
                    // 워크스페이스 정리
                    cleanWs()
                } catch (Exception e) {
                    echo "⚠️ Cleanup warning: ${e.getMessage()}"
                }
            }
        }
        
        success {
            echo '''
            🎉 ==============================
               DEPLOYMENT SUCCESSFUL! 
            ==============================
            
            ✅ Application deployed successfully!
            🌐 Access URL: https://kkori.site
            🏥 Health Check: https://kkori.site/api/actuator/health
            📊 Application Info: https://kkori.site/api/actuator/info
            
            📈 Build Number: ''' + env.BUILD_NUMBER + '''
            🏷️  Docker Image: ''' + env.DOCKER_IMAGE + ''':''' + env.DOCKER_TAG + '''
            
            ==============================
            '''
        }
        
        failure {
            echo '''
            💥 ==============================
                DEPLOYMENT FAILED!
            ==============================
            '''
            
            script {
                try {
                    // 실패시 롤백 시도
                    echo "🔄 Attempting rollback to previous version..."
                    sh '''
                        # 실패한 컨테이너 중지
                        docker stop kkori-backend || true
                        docker rm kkori-backend || true
                        
                        # 이전 버전으로 롤백 (latest 태그 사용)
                        docker run -d \\
                            --name kkori-backend \\
                            --restart unless-stopped \\
                            -p 8080:8080 \\
                            -e SPRING_PROFILES_ACTIVE=prod \\
                            -e DB_HOST=$DB_HOST \\
                            -e DB_PORT=$DB_PORT \\
                            -e DB_NAME=$DB_NAME \\
                            -e DB_USERNAME=$DB_USERNAME \\
                            -e DB_PASSWORD=$DB_PASSWORD \\
                            -e DDL_AUTO=validate \\
                            -e GMS_API_KEY=$GMS_API_KEY \\
                            -e GMS_WHISPER_URL=$GMS_WHISPER_URL \\
                            -e GMS_GPT_URL=$GMS_GPT_URL \\
                            -e JWT_SECRET_KEY=$JWT_SECRET_KEY \\
                            -e JWT_ACCESS_TOKEN_MINUTE_TIME=$JWT_ACCESS_TOKEN_MINUTE_TIME \\
                            -e JWT_REFRESH_TOKEN_MINUTE_TIME=$JWT_REFRESH_TOKEN_MINUTE_TIME \\
                            -e KAKAO_CLIENT_ID=$KAKAO_CLIENT_ID \\
                            -e KAKAO_CLIENT_SECRET=$KAKAO_CLIENT_SECRET \\
                            -e KAKAO_REDIRECT_URL=$KAKAO_REDIRECT_URL \\
                            -e KAKAO_TOKEN_URL=$KAKAO_TOKEN_URL \\
                            ${DOCKER_IMAGE}:latest || echo "Rollback failed - no previous version available"
                    '''
                    echo "🔄 Rollback attempted. Check application status manually."
                } catch (Exception e) {
                    echo "⚠️ Rollback failed: ${e.getMessage()}"
                }
            }
        }
    }
}