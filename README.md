# Dataround link

Dataround link is an **open-source data integration tool** designed for multi-source heterogeneous data synchronization. It supports seamless integration and synchronization of **structured, semi-structured, and unstructured data**.

Dataround link adopts a **zero-code visual** design philosophy, eliminating the need for configuration files or programming. Users can easily configure and manage complex data synchronization tasks through an intuitive web interface, truly democratizing data synchronization.

## Key Features

- **Multi-source Heterogeneous Support** - Unified support for structured, semi-structured, and unstructured data sources
- **Structured Data Support** - Traditional relational databases (MySQL, PostgreSQL, Oracle, SQL Server, TiDB, etc.)
- **Semi-structured Data Support** - MongoDB, Kafka, JSON, XML, and more
- **Unstructured Data Support** - Images, videos, model files, documents, and other file types
- **Zero-Code Visual Interface** - No configuration files or programming required, pure visual operations
- **Virtual Table Support** - Map semi-structured data from Kafka, Redis, etc. to structured data through virtual tables, enabling synchronization with relational databases
- **Based on SeaTunnel** - Dataround link table data synchronization is implemented based on Apache SeaTunnel, providing stable and reliable data transmission capabilities



## Product Screenshots

### Job Management Dashboard
![Job Management](docs/imgs/joblist.png)

### Table Mapping Interface
![Table Mapping](docs/imgs/tablemapping.png)


## Building dataround link from source

Prerequisites for building Flink:

- Java 17 or higher
- Maven 3.8 or higher

1. Clone the repository:
```bash
git clone https://github.com/dataround/dataround-link.git
cd dataround-link
```

2. Build the backend:
```bash
cd dataround-link
mvn clean package -DskipTests
```

3. Start dataround link server:
```bash
tar zxvf dataround-link-1.0.tar.gz
cd dataround-link-1.0
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

For support, please contact yuehan124@gmail.com or open an issue in the GitHub repository.

wechat support
![wechat qr code](docs/imgs/author_wechat.png)

Scan the QR code above to add us on WeChat for direct support and communication.

## Acknowledgments

Thanks to all contributors who have helped make Dataround link better! 