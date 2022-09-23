FROM rust:1.64-slim-buster as build

# create a new empty shell project
RUN USER=root cargo new --bin the-untitled-cloud-webftp
WORKDIR /the-untitled-cloud-webftp

# copy over your manifests
COPY ./Cargo.lock ./Cargo.lock
COPY ./Cargo.toml ./Cargo.toml

# this build step will cache your dependencies
RUN cargo build --release
RUN rm src/*.rs

# copy your source tree
COPY ./src ./src
COPY ./Rocket.toml ./Rocket.toml

ARG JWT_SECRET
ARG DATA_FOLDER

# build for release
RUN rm ./target/release/deps/the_untitled_cloud_webftp*
RUN cargo build --release

# our final base
FROM rust:1.64-slim-buster

# copy the build artifact from the build stage
COPY --from=build /the-untitled-cloud-webftp/target/release/the-untitled-cloud-webftp .

ENV ROCKET_ADDRESS=0.0.0.0
EXPOSE 8000

# set the startup command to run your binary
CMD ["./the-untitled-cloud-webftp"]