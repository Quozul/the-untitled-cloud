# Minecraft Host API

## Environment variables
```sh
DOCKER_HOST=unix://~/.docker/desktop/docker.sock # For Docker Desktop, replace ~/ with absolute path
STRIPE_API_ENDPOINT_SECRET=whsec_ # Stripe's webhook endpoint secret
STRIPE_API_KEY=sk_test_ # Stripe private key
```

## Run

```sh
# Gradle task
build -t -x test -i

# Ktor run program arguments
-config=src/main/resources/application-dev.conf
```
