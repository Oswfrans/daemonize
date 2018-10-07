# Pull base image
FROM payvision/sbt:1.2.3

# Copy files to temp folder
COPY --chown=root:root target/universal/*.tgz /tmp/

# Make app directory
RUN \
  mkdir -p /app

# Unzip file
RUN \
  tar -C /app -xf /tmp/*.tgz --strip-components=1

# Clean up
RUN \
  rm -rf /tmp/*.tgz

# Exposed port
EXPOSE 9000/tcp

# Define working directory
WORKDIR /app

# Define executable
ENTRYPOINT ["/app/bin/ce-optimize-api"]
