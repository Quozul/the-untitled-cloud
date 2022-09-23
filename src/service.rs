use rocket::http::Status;
use rocket::response::status::Custom;
use crate::claims::Claims;
use rocket::serde::uuid::Uuid;
use rocket_db_pools::{Connection};
use crate::TheUntitledCloud;
use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize)]
pub struct SubscriptionItem {
    id: String,
    subscription: String,
    product: i32,
    container: String,
    ftpPassword: String,
}

#[get("/<service_id>")]
pub async fn service(token: Claims, mut db: Connection<TheUntitledCloud>, service_id: Uuid) -> Result<String, Custom<String>> {
    let row = rocket_db_pools::sqlx::query("select i.id from subscription_item i join subscription s on s.id = i.subscription where s.owner = $1 and i.id = $2;")
        .bind(token.id)
        .bind(service_id)
        .fetch_one(&mut *db).await
        .ok();

    match row {
        Some(t) => {
            Ok(format!("{}", "ok"))
        }
        None => {
            Err(Custom(Status::NotFound, String::from("Container not found")))
        }
    }
}