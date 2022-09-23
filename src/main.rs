mod claims;
mod service;
mod list;

#[macro_use]
extern crate rocket;

use rocket::fairing::{Fairing, Info, Kind};
use rocket::{Request, Response};
use rocket::http::Header;
use crate::claims::jwt;
use rocket_db_pools::{sqlx, Database};

#[derive(Database)]
#[database("theuntitledcloud")]
pub struct TheUntitledCloud(sqlx::PgPool);

#[get("/")]
fn index() -> &'static str {
    "Hello, world!"
}

pub struct CORS;

#[rocket::async_trait]
impl Fairing for CORS {
    fn info(&self) -> Info {
        Info {
            name: "Add CORS headers to responses",
            kind: Kind::Response,
        }
    }

    async fn on_response<'r>(&self, _request: &'r Request<'_>, response: &mut Response<'r>) {
        println!("Setting access control allow origin");
        response.set_header(Header::new("Access-Control-Allow-Origin", "*"));
        response.set_header(Header::new("Access-Control-Allow-Methods", "POST, GET, PATCH, OPTIONS"));
        response.set_header(Header::new("Access-Control-Allow-Headers", "*"));
        response.set_header(Header::new("Access-Control-Allow-Credentials", "true"));
    }
}

#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let _rocket = rocket::build()
        .attach(TheUntitledCloud::init())
        .attach(CORS)
        .mount("/", routes![index, jwt, service::service, service::service_options])
        .launch()
        .await?;

    Ok(())
}