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
STRIPE_SK=sk_test_
STRIPE_WHOOK=whsec_
STRIPE_PK=pk_test_

# Docker configuration
DOCKER_SOCK=~/.docker/desktop/docker.sock # Where the docker socket is on your computer
DOCKER_HOST=unix:///var/run/docker.sock # Should never be modified
CONTAINER_DIRECTORY=/home/mchost

# Database configuration
DATABASE_NAME=mc_host
DATABASE_USER=postgres
DATABASE_PASS=postgrespw

# App configuration
API_ENDPOINT=http://localhost/api
```

## Start
```shell
docker compose up -d --build
```