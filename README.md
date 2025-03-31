# 도커 DB 실행 방법

docker run -d \
--name wedvice \
-e POSTGRES_DB=wedvice-db \
-e POSTGRES_USER=wedviceuser \
-e POSTGRES_PASSWORD=wedvicepass \
-p 5432:5432 \
postgres:14

# 서버 실행 방법
./gradlew bootrun