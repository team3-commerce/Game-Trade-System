docker compose -f .\misc\docker-compose-test-all.yml down
docker compose -f .\misc\docker-compose-test-all.yml up -d
echo "DB 시작을 기다리는 중"
timeout /t 10
call gradlew testAll
docker compose -f .\misc\docker-compose-test-all.yml down

