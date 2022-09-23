mod claims;

#[macro_use] extern crate rocket;

use crate::claims::jwt;

#[get("/")]
fn index() -> &'static str {
    "Hello, world!"
}

#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    let _rocket = rocket::build()
        .mount("/", routes![index, jwt])
        .launch()
        .await?;

    Ok(())
}