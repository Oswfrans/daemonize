# Pull base image
FROM openjdk:8-alpine

# Install bash
RUN apk update && apk --no-cache --update add bash

# Copy files to temp folder
COPY --chown=root:root target/universal/*.tgz /tmp/
#COPY --chown=root:root target/universal/*.tgz /tmp/
#COPY -Path target/scala-2.11/*.tgz -Destination /tmp/

# Make app directory
RUN mkdir -p /app

# Unzip file
RUN tar -C /app -xf /tmp/*.tgz --strip-components=1

# Clean up
RUN rm -rf /tmp/*.tgz

# Exposed port
EXPOSE 80/tcp

# Define working directory
WORKDIR /app

# Define executable
ENTRYPOINT ["/app/bin/ce-optimize-api", "-Dhttp.port=80"]

#this is for local

# Pull base image
#FROM openjdk:8-alpine

# Make app directory
#RUN mkdir -p /app

#sicko mode
#ADD . /app

# Exposed port
#EXPOSE 80/tcp

# Define working directory
#WORKDIR /app

# Define executable
#ENTRYPOINT ["/app/bin/ce-optimize-api", "-Dhttp.port=80"]