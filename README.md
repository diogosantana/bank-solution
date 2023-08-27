# Bank Solution

## Test

```bash
cd bank-solution
./mvnw test
```

## Run with Maven wrapper

```bash
cd bank-solution
./mvnw spring-boot:run
```

## Run with docker

```bash
docker build -t bank-solution .
docker run -it --rm -p 8080:8080 bank-solution
```

## Run with docker-compose

```bash
docker compose up
```

## Postman Collection

[bank-solution.postman_collection.json](./bank-solution.postman_collection.json)

## Roadmap

- Give the balance on `/api/transactions/history`
- Add security
