# OrderProcessor
An asynchronous order processing system that separates fast order intake from background fulfillment. 

## Architecture 
<img width="3262" height="902" alt="F73C26BF-D481-4469-A2A5-454A5EBE6303_1_201_a" src="https://github.com/user-attachments/assets/5363198f-94dd-4261-9fa1-0c2fe210a0ce" />

Flow : A client submits an order > the API validates inventory and saves the order as PENDING, responding immediately > the order ID is published to a Redis stream > the Rust worker picks it up, simulates fulfillment work, and updates the orders status through PROCESSED to SHIPPED in Postgres.

## Getting started & Prerequisites 
1) Docker desktop
2) JDK 21+
3) Rust (via rustup)

1. Start Postgres and Redis
```bash
docker run -d --name orders-postgres -e POSTGRES_PASSWORD=devpass -p 5433:5432 postgres
docker run -d --name orders-redis -p 6379:6379 redis
```
2. Run the API
```bash
cd api
./mvnw spring-boot:run
```
3.Run the worker 
```bash
cd worker
export DATABASE_URL="postgres://postgres:devpass@localhost:5433/postgres"
cargo run
```
4. Add a test product
```bash
docker exec -it orders-postgres psql -U postgres -c \
  "INSERT INTO products (id, name, price, stock_quantity) VALUES (gen_random_uuid(), 'Widget', 9.99, 100);"
```
Grab it's ID:
```bash
docker exec -it orders-postgres psql -U postgres -c "SELECT id, name FROM products;"
```

5. Place an order
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail": "test@example.com", "items": [{"productId": "PASTE_PRODUCT_ID_HERE", "quantity": 2}]}'
```

## Roadmap 
This is an active, in progress project. Remaining work, roughly in order :

1) Complete end to end testing of the rust worker
2) Add a GET /orders/{id} endpoint to check order status
3) Build a small react dashboard to place orders and watch status update live
4) Load test the API with locust and publish real throughput / latency numbers
5) Add a docker-compose.yml so the whole stack starts with one command
6) Write dockerfiles for the API and worker themselves
7) Deploy the live demo
8) Add automated tests for the API's stock validation logic


