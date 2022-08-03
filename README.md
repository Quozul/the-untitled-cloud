# Minecraft Host API

## Environment variables
```sh
# Stripe configuration
STRIPE_API_KEY=sk_test_...
STRIPE_API_ENDPOINT_SECRET=whsec_...

# Docker configuration
DOCKER_HOST=unix:///var/run/docker.sock # Where the docker socket is on your computer
CONTAINER_DIRECTORY=/home/mchost/servers/ # Where to store persistent data with a trailing slash

# Database configuration
DATABASE_URL="jdbc:postgresql://localhost:5432/${DATABASE_NAME}"
DATABASE_USER=postgres
DATABASE_PASS=postgrespw

# SMTP configuration
SMTP_USER=username
SMTP_PASS=password
SMTP_HOST=smtp.example.com
SMTP_PORT=587
SMTP_FROM_EMAIL=no-reply@example.com
SMTP_FROM_NAME=No reply

# Security, just put some random bytes there
JWT_SECRET=secret
SALT=salt # For stronger password hashing
PEPPER=pepper
```

## Run

```sh
# Gradle task
build -t -x test -i

# Ktor run program arguments
-config=src/main/resources/application-dev.conf
```
