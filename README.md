# Minecraft Host API

## Run

```sh
# Gradle task
build -t -x test -i

# Ktor run program arguments
-config=src/main/resources/application-dev.conf
```

## Environment variables
* `STRIPE_API_KEY`: Private Stripe API key  
* `STRIPE_API_ENDPOINT_SECRET`: Webhook secret  
* `DOCKER_HOST`: For Docker Desktop `unix:///~/.docker/desktop/docker.sock`  
