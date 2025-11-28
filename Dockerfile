# Multi-stage build to reduce image size
# Stage 1: Preparer stage - extract application files
FROM eclipse-temurin:17-jdk-jammy AS preparer

ENV DATAROUND_HOME=/opt/dataround-link

# Copy and extract the built tar.gz package
COPY dataround-link-svc/target/dataround-link-*.tar.gz $DATAROUND_HOME/
RUN tar -xzf $DATAROUND_HOME/dataround-link-*.tar.gz -C $DATAROUND_HOME --strip-components=1 \
    && rm $DATAROUND_HOME/dataround-link-*.tar.gz

# Stage 2: Runtime stage (this becomes the final image)
FROM eclipse-temurin:17-jre-jammy

ENV DATAROUND_HOME=/opt/dataround-link \
    TZ=Asia/Shanghai \
    POSTGRES_DB=dataround_link \
    POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=dataround.io

# Install necessary tools and set up directories
RUN apt-get update && apt-get install -y \
    postgresql \
    postgresql-contrib \
    sudo \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /var/lib/postgresql/data \
    && chown -R postgres:postgres /var/lib/postgresql \
    && mkdir -p $DATAROUND_HOME

# Copy only the extracted application files from preparer stage (excluding the tar.gz)
COPY --from=preparer $DATAROUND_HOME $DATAROUND_HOME

# Set working directory
WORKDIR $DATAROUND_HOME

# Configure database and PostgreSQL cluster
RUN sed -i "s/password: \".*\"/password: \"$POSTGRES_PASSWORD\"/g" conf/application.yaml \
    && service postgresql start \
    && sleep 5 \
    && sudo -u postgres psql --command "ALTER USER postgres PASSWORD '$POSTGRES_PASSWORD';" \
    && sudo -u postgres createdb -E UTF8 $POSTGRES_DB \
    && service postgresql stop \
    && PG_VERSION=$(pg_config --version | grep -oE '[0-9]+' | head -1) \
    && if [ ! -d "/etc/postgresql/$PG_VERSION/main" ]; then \
        pg_createcluster $PG_VERSION main; \
    fi \
    && sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/g" /etc/postgresql/$PG_VERSION/main/postgresql.conf \
    && echo "host all all 0.0.0.0/0 md5" >> /etc/postgresql/$PG_VERSION/main/pg_hba.conf

# Create startup script
RUN echo '#!/bin/bash' > /opt/start-services.sh \
    && echo 'set -e' >> /opt/start-services.sh \
    && echo '# Start PostgreSQL' >> /opt/start-services.sh \
    && echo 'service postgresql start' >> /opt/start-services.sh \
    && echo '# Wait for PostgreSQL to start' >> /opt/start-services.sh \
    && echo 'sleep 5' >> /opt/start-services.sh \
    && echo '# Start dataround-link-svc' >> /opt/start-services.sh \
    && echo './bin/start.sh --foreground' >> /opt/start-services.sh \
    && chmod +x /opt/start-services.sh

# Expose ports (PostgreSQL 5432 and dataround-link-svc 5600)
EXPOSE 5432 5600

ENTRYPOINT ["/opt/start-services.sh"]