CONTAINER_NAME_POSTGREE=postgres

up:
	cd ./docker-compose && docker compose -f application.yml -f localstack.yml up -d

down:
	cd ./docker-compose && docker compose -f application.yml -f database.yml -f localstack.yml down --remove-orphans

config:
	cd ./docker-compose && docker compose -f database.yml -f localstack.yml up -d

db-seed:
	docker cp src/main/resources/db/seed/initial_load.sql $(CONTAINER_NAME_POSTGREE):/tmp/ && docker exec -it $(CONTAINER_NAME_POSTGREE) psql -U postgres -d events_time -f /tmp/initial_load.sql
