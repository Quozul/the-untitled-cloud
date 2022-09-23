use std::cmp::Ordering;
use std::path::{Path, PathBuf};
use rocket::http::Status;
use rocket::response::status::Custom;
use crate::claims::Claims;
use rocket::serde::uuid::Uuid;
use rocket_db_pools::{Connection};
use crate::list::{FileType, list};
use crate::TheUntitledCloud;
use rocket::serde::json::serde_json::json;
use rocket::serde::json::Value;

#[get("/<service_id>/<path..>")]
pub async fn service(token: Claims, mut db: Connection<TheUntitledCloud>, service_id: Uuid, path: PathBuf) -> Result<Value, Custom<String>> {
    let row = rocket_db_pools::sqlx::query("select i.id from subscription_item i join subscription s on s.id = i.subscription where s.owner = $1 and i.id = $2;")
        .bind(token.id)
        .bind(service_id)
        .fetch_one(&mut *db).await
        .ok();

    // TODO: Base path must be in env
    let np = Path::new("/home/mchost/servers/").join(service_id.to_string()).join(path);

    println!("{}", np.to_str().unwrap());

    match row {
        Some(_) => {
            let mut entries = list(&np);

            // Sort directory first
            entries.sort_by(|a, b| if a.file_type == b.file_type {
                a.name.cmp(&b.name)
            } else if a.file_type == FileType::DIRECTORY {
                Ordering::Less
            } else {
                Ordering::Greater
            });

            Ok(json!(entries))
        }
        None => {
            Err(Custom(Status::NotFound, String::from("Container not found")))
        }
    }
}

#[options("/<service_id>/<path..>")]
pub async fn service_options(service_id: Uuid, path: PathBuf) -> Status {
    Status::Accepted
}