CONTAINER_NAME_POSTGREE=postgres

up:
	cd ./docker-compose && docker compose -f database.yml up -d

down:
	cd ./docker-compose && docker compose -f database.yml down --remove-orphans

kafka-topics-list:
	docker exec broker bash -c "kafka-topics --list --bootstrap-server broker:29092"

kafka-producer:
	docker exec -it broker kafka-console-producer.sh --topic $(TOPIC) --bootstrap-server localhost:9092

code-analysis-local-sonarqube:
	cd ./devsecops && sudo ./code-analysis-local.sh

db-seed:
	docker cp src/main/resources/db/seed/initial_load.sql $(CONTAINER_NAME_POSTGREE):/tmp/ && docker exec -it $(CONTAINER_NAME_POSTGREE) psql -U postgres -d events_time -f /tmp/initial_load.sql
