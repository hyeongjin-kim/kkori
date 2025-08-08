# ===== Build Stage =====
FROM openjdk:21-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle wrapper와 빌드 파일들 먼저 복사 (캐싱 최적화)
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Gradle wrapper 파일 확인
RUN ls -la gradle/wrapper/

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (소스 코드 변경시 재빌드 방지)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src src

# JAR 파일 빌드
RUN ./gradlew bootJar --no-daemon --stacktrace

# 빌드 결과 확인
RUN ls -la build/libs/

# ===== Runtime Stage =====
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 패키지 설치 (curl for health check, ca-certificates for SSL)
RUN apt-get update && \
    apt-get install -y curl ca-certificates tzdata && \
    rm -rf /var/lib/apt/lists/*

# 타임존 설정 (한국 표준시)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 보안을 위한 non-root 사용자 생성
RUN groupadd --system --gid 1001 kkori && \
    useradd --system --uid 1001 --gid kkori --shell /bin/bash --create-home kkori

# 빌드 단계에서 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 파일 소유권 변경
RUN chown kkori:kkori app.jar

# non-root 사용자로 전환
USER kkori

# 포트 노출
EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
