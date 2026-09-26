# OrderProcessor
An asynchronous order processing system that separates fast order intake from background fulfillment. 

# Architecture 
<img width="3262" height="902" alt="F73C26BF-D481-4469-A2A5-454A5EBE6303_1_201_a" src="https://github.com/user-attachments/assets/5363198f-94dd-4261-9fa1-0c2fe210a0ce" />

Flow : A client submits an order > the API validates inventory and saves the order as PENDING, responding immediately > the order ID is published to a Redis stream > the Rust worker picks it up, simulates fulfillment work, and updates the orders status through PROCESSED to SHIPPED in Postgres.


# Roadmap 
This is an active, in progress project. Remaining work, roughly in order :

1) Complete end to end testing of the rust worker
2) Add a GET /orders/{id} endpoint to check order status
3) Build a small react dashboard to place orders and watch status update live
4) Load test the API with locust and publish real throughput / latency numbers
5) Add a docker-compose.yml so the whole stack starts with one command
6) Write dockerfiles for the API and worker themselves
7) Deploy the live demo
8) Add automated tests for the API's stock validation logic


