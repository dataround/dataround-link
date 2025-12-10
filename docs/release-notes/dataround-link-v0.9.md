# Dataround Link v0.9.0 Release Notes

We're excited to announce the release of Dataround Link v0.9.0! This is a significant milestone release that delivers robust data integration capabilities with support for synchronizing multiple data sources.

## About Dataround Link

Dataround Link is an open-source data integration tool designed for multi-source heterogeneous data synchronization. It supports seamless integration and synchronization of structured, semi-structured, and unstructured data. Adopting a zero-code visual design philosophy, it eliminates the need for configuration files or programming, allowing users to easily configure and manage complex data synchronization tasks through an intuitive web interface.

## Core Features

- **Multi-source Heterogeneous Support** - Unified support for structured, semi-structured, and unstructured data sources
- **Structured Data Support** - Traditional relational databases (MySQL, PostgreSQL, Oracle, SQL Server, TiDB, etc.)
- **Semi-structured Data Support** - Kafka, JSON, and more
- **Unstructured Data Support** - Images, videos, model files, documents, and other file types
- **Zero-Code Visual Interface** - No configuration files or programming required, pure visual operations
- **Virtual Table Support** - Map semi-structured data from Kafka, JSON, etc. to structured data through virtual tables, enabling synchronization with relational databases
- **Based on SeaTunnel** - Table data synchronization is implemented based on Apache SeaTunnel, providing stable and reliable data transmission capabilities

## Deployment Options

### Docker Deployment (Recommended)

Using the default embedded H2 database:

```bash
docker run -p 5600:5600 -d dataround/link:0.9.0
```

Using an external PostgreSQL database:

```bash
docker run -e SPRING_PROFILES_ACTIVE=prod \
  -e DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5432/dataround_link \
  -e DATASOURCE_USERNAME=your_username \
  -e DATASOURCE_PASSWORD=your_password \
  -p 5600:5600 -d \
  dataround/link:0.9.0
```

### Building from Source

Requirements:
- Java 17 or higher
- Maven 3.8 or higher

Build steps:

```bash
git clone https://github.com/dataround/dataround-link.git
cd dataround-link
mvn clean package -DskipTests
```

The deployment package will be generated at: `dataround-link-svc/target/dataround-link-0.9.0.tar.gz`

## Database Initialization

Dataround Link supports both PostgreSQL and H2 databases:

**PostgreSQL (Production)**:

- Create database: `CREATE DATABASE dataround_link;`
- Tables are automatically created on service startup

**H2 (Development/Testing)**:
- No installation required, H2 is configured to persist data to local files
- Tables are automatically created on service startup

## Starting the Service

**PostgreSQL (Production)**:
```bash
./bin/start.sh
```

**H2 Database (Development/Testing)**:
```bash
./bin/start.sh --spring.profiles.active=test
```

Access URL: http://localhost:5600/datalink

## Supported Connectors

- JDBC Connector (supports major relational databases)
- Hive Connector
- Kafka Connector
- File Connector (supports FTP/SFTP)

## Feedback and Contributions

If you encounter any issues or have suggestions during usage, please contact us through:

1. GitHub Issues: https://github.com/dataround/dataround-link/issues
2. GitHub Discussions: https://github.com/dataround/dataround-link/discussions

We welcome all forms of contributions, including but not limited to code submissions, documentation improvements, and feature suggestions.

Thank you for choosing Dataround Link!