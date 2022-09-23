use jsonwebtoken::{decode, DecodingKey, Validation};
use rocket::{Request, request};
use rocket::http::{Status};
use rocket::outcome::Outcome;
use rocket::request::FromRequest;
use serde::{Serialize, Deserialize};
use uuid::Uuid;

#[derive(Debug)]
pub enum ApiTokenError {
    Missing,
    Invalid,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    aud: String,
    iss: String,
    pub id: Uuid,
    exp: usize,
    email: String,
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for Claims {
    type Error = ApiTokenError;

    async fn from_request(request: &'r Request<'_>) -> request::Outcome<Self, Self::Error> {
        let token = request.headers().get_one("authorization");
        let secret = env!("JWT_SECRET");

        match token {
            Some(token) => {
                // Validate token
                let decoded = decode::<Claims>(
                    &token[7..], // Remove "Bearer "
                    &DecodingKey::from_secret(secret.as_ref()),
                    &Validation::default()
                );
                match decoded {
                    Ok(response) => {
                        Outcome::Success(response.claims)
                    }
                    Err(_) => {
                        Outcome::Failure((Status::Unauthorized, ApiTokenError::Invalid))
                    }
                }
            }
            None => Outcome::Failure((Status::Unauthorized, ApiTokenError::Missing)),
        }
    }
}

// Just an endpoint to test JWT validation
#[get("/jwt")]
pub fn jwt(token: Claims) -> String {
    format!("{}", token.id)
}