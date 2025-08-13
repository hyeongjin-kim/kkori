pipeline {
    agent any
    
    environment {
        // Docker 이미지 정보
        DOCKER_IMAGE = "jin0410/kkori-backend"
        DOCKER_TAG = "${BUILD_NUMBER}"
        
        // Jenkins에 저장된 credential ID들
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        
        // RDS 연결 정보 - Jenkins Credentials에서 보안 관리
        DB_HOST = credentials('mysql-host')  // Jenkins Secret 사용
        DB_PORT = "3306"
        DB_NAME = "kkori"
        DB_USERNAME = credentials('mysql-username')
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
                script {
                    // NodeJS 도구 설정
                    def nodeJS = tool name: 'NodeJS-22', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
                    env.PATH = "${nodeJS}/bin:${env.PATH}"
                }
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
                        # 테스트 실행 (prod 프로파일 사용, H2 테스트 DB 사용)
                        export SPRING_PROFILES_ACTIVE=prod
                        # H2 테스트 DB 설정
                        export DB_URL="jdbc:h2:mem:kkori-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;DATABASE_TO_LOWER=TRUE"
                        export DB_DRIVER_CLASS_NAME="org.h2.Driver"
                        export DB_USERNAME=sa
                        export DB_PASSWORD=""
                        export DDL_AUTO=create-drop
                        export DB_DIALECT="org.hibernate.dialect.H2Dialect"
                        export DB_PLATFORM="org.hibernate.dialect.H2Dialect"
                        export HIBERNATE_SHOW_SQL="false"
                        export HIBERNATE_FORMAT_SQL="false"
                        # API 키들 사용 (Jenkins Credentials에서 가져옴)
                        export GMS_API_KEY=${GMS_API_KEY}
                        export GMS_WHISPER_URL=${GMS_WHISPER_URL}
                        export GMS_GPT_URL=${GMS_GPT_URL}
                        export JWT_SECRET_KEY=${JWT_SECRET_KEY}
                        export JWT_ACCESS_TOKEN_MINUTE_TIME=${JWT_ACCESS_TOKEN_MINUTE_TIME}
                        export JWT_REFRESH_TOKEN_MINUTE_TIME=${JWT_REFRESH_TOKEN_MINUTE_TIME}
                        export KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}
                        export KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET}
                        export KAKAO_REDIRECT_URL=${KAKAO_REDIRECT_URL}
                        export KAKAO_TOKEN_URL=${KAKAO_TOKEN_URL}
                        # 앱 설정 - 테스트용 로컬 환경
                        export FRONTEND_URL="http://localhost:3000"
                        export CORS_ALLOWED_ORIGINS="http://localhost:3000"
                        # Docker 컨테이너에서 테스트 실행시 네트워킹 옵션 설정
                        export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Djava.net.useSystemProxies=false"
                        # 네트워크 인터페이스 정보 확인
                        echo "Network interfaces:"
                        ip addr show || ifconfig || echo "Network info not available"
                        # 테스트 실행 (integration 태그 제외 - 실제 파일이 필요한 테스트)
                        ./gradlew clean test --no-daemon --stacktrace -PexcludeTags=integration
                    '''
                }
            }
            post {
                always {
                    // 테스트 결과 리포트 (junit 사용)
                    junit testResults: 'backend/kkori/build/test-results/test/*.xml', allowEmptyResults: true

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
                        # JAR 파일 빌드 (테스트 건너뛰기)
                        ./gradlew bootJar --no-daemon --stacktrace -x test

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
                script {
                    // NodeJS 도구 설정 (다시 한 번 확실히)
                    def nodeJS = tool name: 'NodeJS-22', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
                    env.PATH = "${nodeJS}/bin:${env.PATH}"
                }
                dir('frontend') {
                    withCredentials([file(credentialsId: 'frontend-env-file', variable: 'ENV_FILE')]) {
                        sh '''
                            # frontend 디렉토리 안에 .env 파일 복사
                            cp $ENV_FILE .env
                            
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
                            
                            # 보안을 위해 .env 파일 삭제
                            rm -f .env
                        '''
                    }
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

                            # 8080 포트를 사용 중인 다른 컨테이너도 확인
                            echo "Checking containers using port 8080..."
                            docker ps --filter "publish=8080" || true

                            echo "Cleaning up unused images..."
                            docker image prune -f
                        '''

                        // 새 컨테이너 실행
                        sh """
                            echo "Starting new container..."
                            docker run -d \\
                                --name kkori-backend \\
                                --restart unless-stopped \\
                                --expose 8080 \\
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
                        // 빌드된 파일을 호스트로 복사 후 SSH로 배포
                        sh '''
                            # 빌드 결과 확인
                            echo "Frontend build artifacts:"
                            ls -la frontend/dist/

                            # SSH를 통해 호스트에 배포 (Jenkins 컨테이너에서 호스트로)
                            # 호스트의 Docker 소켓을 통해 호스트 명령 실행
                            # Jenkins 컨테이너의 볼륨을 직접 사용
                            docker run --rm \
                                --volumes-from jenkins \
                                -v /var/www/kkori/frontend:/target \
                                alpine:latest sh -c '
                                    echo "=== Debugging Info ==="
                                    echo "Finding frontend dist directory..."
                                    find /var/jenkins_home -name "dist" -type d | head -5

                                    # Jenkins 워크스페이스에서 dist 디렉토리 찾기
                                    DIST_PATH=$(find /var/jenkins_home/workspace -name "dist" -path "*/frontend/dist" | head -1)
                                    echo "Found dist path: $DIST_PATH"

                                    if [ -n "$DIST_PATH" ] && [ -d "$DIST_PATH" ]; then
                                        echo "Source directory contents:"
                                        ls -la "$DIST_PATH/"

                                        # 디렉토리 생성
                                        mkdir -p /target

                                        # 기존 파일 제거
                                        rm -rf /target/* 2>/dev/null || true

                                        # 파일 복사
                                        cp -r "$DIST_PATH"/. /target/
                                        echo "✅ Files copied successfully"

                                        # 배포 결과 확인
                                        echo "Frontend deployment status:"
                                        ls -la /target/
                                    else
                                        echo "❌ Could not find frontend dist directory"
                                        echo "Available workspaces:"
                                        ls -la /var/jenkins_home/workspace/ || echo "No workspace found"
                                        exit 1
                                    fi
                                '

                            # nginx 설정 테스트 및 리로드 (호스트에서 실행)
                            docker run --rm --pid=host --privileged \
                                alpine:latest sh -c '
                                    # nginx 프로세스에 reload 신호 전송
                                    nsenter -t 1 -m -u -n -i -p -- nginx -t && \
                                    nsenter -t 1 -m -u -n -i -p -- nginx -s reload
                                ' || echo "⚠️ Nginx reload failed - please reload manually"

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
            🏥 Health Check: https://kkori.site/actuator/health
            📊 Application Info: https://kkori.site/actuator/info

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
