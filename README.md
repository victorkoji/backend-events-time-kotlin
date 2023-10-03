# backend-events-time

## Requisitos
- [Docker] (https://www.docker.com/)
- [Makefile] (https://sourceforge.net/projects/gnuwin32/files/latest/download)
- [Dbeaver] (https://dbeaver.io/) ou outro gerenciador de banco

## Tecnologias
- [Kotlin] (https://kotlinlang.org/)
- [Spring Boot] (https://spring.io/projects/spring-boot/)

## Setup

### Rodar aplicação com o docker

1. Rodar o projeto para testes:
```
  make up
```

### Rodar aplicação para desenvolvimento local

1. Rodar as configurações do projeto para desenvolvimento
```
  make config
```

2. Caso seja necessário popular o banco manualmente com dados, executar
```
  make db-seed
```

### Documentação com Swagger
- A API está rodando na porta 5000, você pode acessar os endpoints na url http://localhost:5000/docs
