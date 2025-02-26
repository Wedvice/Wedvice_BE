# 1️⃣ Java 17 런타임 환경 제공
FROM openjdk:17-jdk-slim

# 2️⃣ 작업 디렉토리 생성
WORKDIR /app

# 3️⃣ JAR 파일 복사
COPY build/libs/*.jar app.jar

# 4️⃣ 8080 포트 노출
EXPOSE 8080

# 5️⃣ 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]