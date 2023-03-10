## Clone
Download submodules:
```shell
git submodule update --init --recursive
```

Update submodules:
```shell
git submodule update --remote --merge
```

## Environment variables
```shell
# Stripe configuration
STRIPE_SK=sk_test_...
STRIPE_WHOOK=whsec_...
STRIPE_PK=pk_test_...

# Docker configuration
CONTAINER_DIRECTORY=/home/mchost/servers/ # Where to store persistent data with a trailing slash

# Database configuration
DATABASE_NAME=mc_host
DATABASE_USER=postgres
DATABASE_PASS=postgrespw # Make sure that this is secure
DATABASE_VOLUME=/home/mchost/database/ # Where the database's data will be persisted

# App configuration
PORT=80 # The port to listen
API_ENDPOINT=http://localhost:${PORT}/api/ # Trailing slash is mandatory
WS_ENDPOINT=ws://localhost:${PORT}/api/ # Endpoint of the websocket server

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

# Environment
PUBLIC_ADDRESS=127.0.0.1 # Public address of the host machine

# Discord
TOKEN=xxx
CLIENT_SECRET=yyy
CLIENT_ID=123
```

## Start
```shell
# Start all the containers
docker compose up -d --build

# Rebuild only one container
docker compose up -d --build mc_host_front
```