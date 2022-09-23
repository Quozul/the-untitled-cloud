mod claims;
mod service;
mod list;

#[macro_use]
extern crate rocket;

use crate::claims::jwt;
use rocket_db_pools::{sqlx, Database};

#[derive(Database)]
#[database("theuntitledcloud")]
pub struct TheUntitledCloud(sqlx::PgPool);

#[get("/")]
fn index() -> &'static str {
    "Hello, world!"
}

#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let _rocket = rocket::build()
        .attach(TheUntitledCloud::init())
        .mount("/", routes![index, jwt, service::service])
        .launch()
        .await?;

    Ok(())
}