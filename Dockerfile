# Multi-stage build to reduce image size
# Stage 1: Preparer stage - extract application files
FROM eclipse-temurin:17-jdk-jammy AS preparer

ENV DATAROUND_HOME=/opt/dataround-link

# Copy and extract the built tar.gz package
COPY dataround-link-svc/target/dataround-link-*.tar.gz $DATAROUND_HOME/
RUN tar -xzf $DATAROUND_HOME/dataround-link-*.tar.gz -C $DATAROUND_HOME --strip-components=1 \
    && chmod +x $DATAROUND_HOME/bin/*.sh \
    && rm $DATAROUND_HOME/dataround-link-*.tar.gz

# Stage 2: Runtime stage (this becomes the final image)
FROM eclipse-temurin:17-jre-jammy

# Docker compose and k8s should override SPRING_PROFILES_ACTIVE environment variables
ENV DATAROUND_HOME=/opt/dataround-link \
    TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=test

# Copy only the extracted application files from preparer stage
COPY --from=preparer $DATAROUND_HOME $DATAROUND_HOME

# Set working directory
WORKDIR $DATAROUND_HOME

# Expose port (dataround-link-svc 5600)
EXPOSE 5600

ENTRYPOINT ["./bin/start.sh", "--foreground", "--spring.profiles.active=$SPRING_PROFILES_ACTIVE"]