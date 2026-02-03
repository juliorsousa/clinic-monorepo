# Trabalho INF012 2025.2

## Para rodar, é necessário realizar os seguintes passos:

Na pasta frontend/ executar os seguintes comandos:

```bash
npm i
npm run build
npm run preview
```
Em seguida, devemos subir os containers de dependência (PostgreSQL e RabbitMQ) da aplicação. Na pasta backend/ execute os seguintes comandos:

```bash
cd docker/
docker-compose up -d
```

Após ter as dependências executando, iniciar o Eureka:

```bash
mvn -pl server-service spring-boot:run
```

Iniciar o Gateway:

```bash
mvn -pl gateway-service spring-boot:run
```

Iniciar os microsserviços individualmente:

```bash
mvn -pl access-control spring-boot:run
mvn -pl people-management spring-boot:run
mvn -pl appointment-service spring-boot:run
```
