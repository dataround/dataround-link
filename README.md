# Dataround link

Dataround link is an **open-source data integration tool** designed for multi-source heterogeneous data synchronization. It supports seamless integration and synchronization of **structured, semi-structured, and unstructured data**.

Dataround link adopts a **zero-code visual** design philosophy, eliminating the need for configuration files or programming. Users can easily configure and manage complex data synchronization tasks through an intuitive web interface, truly democratizing data synchronization.

## Key Features

- **Multi-source Heterogeneous Support** - Unified support for structured, semi-structured, and unstructured data sources
- **Structured Data Support** - Traditional relational databases (MySQL, PostgreSQL, Oracle, SQL Server, TiDB, etc.)
- **Semi-structured Data Support** - Kafka, JSON, and more
- **Unstructured Data Support** - Images, videos, model files, documents, and other file types
- **Zero-Code Visual Interface** - No configuration files or programming required, pure visual operations
- **Virtual Table Support** - Map semi-structured data from Kafka, JSON, etc. to structured data through virtual tables, enabling synchronization with relational databases
- **Based on SeaTunnel** - Dataround link table data synchronization is implemented based on Apache SeaTunnel, providing stable and reliable data transmission capabilities



## Product Screenshots

### Job Management
![Job Management](docs/imgs/joblist.png)

### Table Mapping
![Table Mapping](docs/imgs/tablemapping.png)


## Building Dataround Link from source

Prerequisites for building Dataround:

- Java 17 or higher
- Maven 3.8 or higher

1. Clone the repository:
```bash
git clone https://github.com/dataround/dataround-link.git
```

2. Build the backend:
```bash
cd dataround-link
mvn clean package -DskipTests
```

The final package will be generated at `dataround-link-svc/target/dataround-link-xxx.tar.gz`


## Deployment

### Database Initialization

Dataround link supports both PostgreSQL and H2 databases:

**Option 1: Using PostgreSQL (default)**
- Install PostgreSQL and create a database:
```sql
CREATE DATABASE dataround_link;
```
- Tables are automatically created on startup

**Option 2: Using H2 (for development/testing)**
- No installation required, H2 is configured to persist data to local files
- Tables are automatically created on startup

### Deployment Options

#### 1. Docker Deployment

**Using default H2 database:**
```bash
docker run -p 5600:5600 -d dataround/link:0.9.0
```

**Using external PostgreSQL database:**
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod \
  -e DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5432/dataround_link \
  -e DATASOURCE_USERNAME=your_username \
  -e DATASOURCE_PASSWORD=your_password \
  -p 5600:5600 -d \
  dataround/link:0.9.0
```

#### 2. Docker Compose Deployment

Create and start services:
```bash
# download docker-compose.yml
curl -O https://raw.githubusercontent.com/dataround/dataround-link/main/docker-compose.yml
# start services
docker-compose up -d
```

This will start both PostgreSQL and Dataround Link services with proper configuration.

#### 3. Kubernetes Deployment

Deploy to Kubernetes cluster:
```bash
kubectl apply -f https://raw.githubusercontent.com/dataround/dataround-link/main/kubernetes.yml
```

This will create:
- PostgreSQL deployment with persistent storage
- Dataround Link deployment with 2 replicas
- Services for both components
- ConfigMaps and Secrets for configuration

#### 4. Manual Deployment

Start Dataround link server:

**For PostgreSQL (production):**
Modify database configuration in environment variables or in `$DATAROUND_HOME/conf/application-prod.yaml`, then start the service:
```bash
./bin/start.sh
```

**For H2 database (testing/development):**
```bash
./bin/start.sh --spring.profiles.active=test
```

The application will be available at `http://localhost:5600/datalink`


## Documentation

For detailed documentation, please refer to the following:
- [Quickstart Guide](https://dataround.io/quickstart/)
- [API Documentation](https://dataround.io/)

## Contributing

We welcome contributions to Dataround link! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Support

If you have any questions or suggestions, we encourage you to:

1. **GitHub Discussions** - Share your thoughts and get help from the community in our [GitHub Discussions](https://github.com/dataround/dataround-link/discussions)
2. **Direct Contact** - Scan the QR code below to join our WeChat support group (please mention "dataround" when joining)

    ![wechat qr code](docs/imgs/author_wechat.png)

## Acknowledgments

Thanks to all contributors who have helped make Dataround link better!