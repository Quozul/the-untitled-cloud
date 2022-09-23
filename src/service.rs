use rocket::http::Status;
use rocket::response::status::Custom;
use crate::claims::Claims;
use rocket::serde::uuid::Uuid;
use rocket_db_pools::{Connection, sqlx};
use crate::TheUntitledCloud;
use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize)]
pub struct SubscriptionItem {
    test: String,
}

#[get("/<id>")]
pub async fn service(token: Claims, mut db: Connection<TheUntitledCloud>, id: Uuid) -> Result<String, Custom<String>> {
    let row: Option<(i64, )> = sqlx::query_as("SELECT $1")
        .bind(150_i64)
        .fetch_one(&mut *db).await
        .ok();

    match row {
        Some(t) => {
            Ok(format!("{} {} {}", token.id, id, t.0))
        }
        None => {
            Err(Custom(Status::NotFound, String::from("Container not found")))
        }
    }
}