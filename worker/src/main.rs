use redis::AsyncCommands;
use redis::streams::StreamReadReply;
use sqlx::postgres::PgPoolOptions;
use std::time::Duration;
use tokio::time::sleep;

const STREAM_KEY: &str = "order_queue";
const GROUP_NAME: &str = "order_workers";
const CONSUMER_NAME: &str = "worker-1";

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("Worker starting...");

    let redis_client = redis::Client::open("redis://127.0.0.1:6379")?;
    let mut redis_conn = redis_client.get_multiplexed_async_connection().await?;

    let _: Result<(), redis::RedisError> = redis_conn
        .xgroup_create_mkstream(STREAM_KEY, GROUP_NAME, "0")
        .await;

    let database_url = std::env::var("DATABASE_URL")?;
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&database_url)
        .await?;

    println!("Connected to Redis and Postgres. Waiting for orders...");

    loop {
        let reply: StreamReadReply = redis_conn
            .xread_options(
                &[STREAM_KEY],
                &[">"],
                &redis::streams::StreamReadOptions::default()
                    .group(GROUP_NAME, CONSUMER_NAME)
                    .count(5)
                    .block(5000),
            )
            .await?;

        for stream_key in reply.keys {
            for entry in stream_key.ids {
                if let Some(order_id_value) = entry.map.get("order_id") {
                    if let redis::Value::Data(bytes) = order_id_value {
                        let order_id = String::from_utf8_lossy(bytes).to_string();
                        println!("Processing order: {}", order_id);

                        process_order(&pool, &order_id).await?;

                        let _: i64 = redis_conn.xack(STREAM_KEY, GROUP_NAME, &[&entry.id]).await?;
                        println!("Order {} completed and acknowledged.", order_id);
                    }
                }
            }
        }
    }
}

async fn process_order(pool: &sqlx::PgPool, order_id: &str) -> Result<(), sqlx::Error> {
    sqlx::query("UPDATE orders SET status = 'PROCESSING', updated_at = now() WHERE id = $1::uuid")
        .bind(order_id)
        .execute(pool)
        .await?;

    sleep(Duration::from_secs(2)).await;

    sqlx::query("UPDATE orders SET status = 'SHIPPED', updated_at = now() WHERE id = $1::uuid")
        .bind(order_id)
        .execute(pool)
        .await?;

    Ok(())
}
