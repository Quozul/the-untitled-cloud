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
DOCKER_SOCK=~/.docker/desktop/docker.sock # Where the docker socket is on your computer
DOCKER_HOST=unix:///var/run/docker.sock # Should never be modified
CONTAINER_DIRECTORY=/home/mchost

# Database configuration
DATABASE_NAME=mc_host
DATABASE_USER=postgres
DATABASE_PASS=postgrespw # Make sure that this is secure

# App configuration
API_ENDPOINT=http://localhost/api/ # Trailing slash is mandatory

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

# Proxy configuration
PORT=80
```

## Start
```shell
# Start all the containers
docker compose up -d --build

# Rebuild only one container
docker compose up -d --build mc_host_front
```