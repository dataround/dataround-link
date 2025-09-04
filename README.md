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


## Building dataround link from source

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

3. Initialize the database:

- Install PostgreSQL and create a database:
```sql
CREATE DATABASE dataround_link;
```
- Run the initialization script to create tables:
```bash
psql -d dataround_link -f $DATAROUND_HOME/conf/init_pg_schema.sql
```

4. Start dataround link server:

Modify database IP, name and password conf in `$DATAROUND_HOME/conf/application.yaml`, Start the service:
```bash
./bin/start.sh
```

The application will be available at `http://localhost:5600/datalink`


## Documentation

For detailed documentation, please refer to the following:
- [User Guide](docs/user-guide.md)
- [Developer Guide](docs/developer-guide.md)
- [API Documentation](docs/api-documentation.md)

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