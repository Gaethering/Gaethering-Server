## Docker 사용법

### 1. .env 파일 생성
root directory에 아래의 형식과 같은 .env 파일 생성
```
TZ=Asia/Seoul
MYSQL_HOST=
MYSQL_PORT=
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=
MYSQL_USER=
MYSQL_PASSWORD=

DATASOURCE_URL=
DATASOURCE_USERNAME=
DATASOURCE_PASSWORD=

MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=

REDIS_HOST=
REDIS_PORT=

JWT_SECRET=

AWS_ACCESS_KEY=
AWS_SECRET_KEY=
AWS_REGION=
AWS_S3_BUCKET=
```

### 2. 실행 가능한 JAR 파일 생성
```bash
./gradlew clean bootJar
```

### 3. docker compose 실행
```bash
docker-compose -f ./docker-compose-dev.yml up -d
```
