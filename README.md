# DataRound Link

DataRound Link is a powerful data integration and synchronization platform that enables seamless data transfer between different databases and systems. What sets it apart is its **zero-code, fully visual** approach to data synchronization - no configuration files or programming required. Users can easily configure and manage complex data synchronization tasks through an intuitive web interface.

## Key Features

- **Zero-Code Data Synchronization**
  - No programming or configuration files needed
  - Visual job configuration through intuitive UI
  - Drag-and-drop interface for field mapping
  - One-click deployment and execution

- **Cross-Platform Data Integration**
  - Support for multiple database types (MySQL, PostgreSQL, Oracle, SQL Server, TiDB, etc.)
  - Heterogeneous data source synchronization
  - Real-time and batch data transfer
  - Flexible data transformation options

- **Database Connection Management**
  - Visual connection testing and validation
  - Secure credential management
  - Connection pool optimization
  - Connection health monitoring

- **Job Management**
  - Visual job configuration
  - Flexible scheduling options
  - Real-time job monitoring
  - Job history and metrics

- **Virtual Table Management**
  - Create and manage virtual tables
  - Field mapping and transformation
  - Data preview and validation

- **User Interface**
  - Modern, responsive web interface
  - Multi-language support (English/Chinese)
  - Intuitive navigation and operation
  - Real-time monitoring dashboard

## Project Structure

```
dataround-link/
├── dataround-link-common/     # Common utilities and shared code
├── dataround-link-connector/  # Database connectors
├── dataround-link-svc/        # Backend service
└── dataround-link-web/        # Frontend application
```

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- Maven 3.8 or higher
- Yarn package manager
- Modern web browser

## Quick Start

1. Clone the repository:
```bash
git clone https://github.com/dataround/dataround-link.git
cd dataround-link
```

2. Build the project:
```bash
mvn clean install
```

3. Start the backend service:
```bash
cd dataround-link-svc
mvn spring-boot:run
```

4. Start the frontend development server:
```bash
cd dataround-link-web
yarn install
yarn start
```

The application will be available at `http://localhost:5600/datalink`

## Building for Production

1. Build the backend:
```bash
mvn clean package -Pprod
```

2. Build the frontend:
```bash
cd dataround-link-web
yarn run build
```

## Documentation

For detailed documentation, please refer to the following:
- [User Guide](docs/user-guide.md)
- [Developer Guide](docs/developer-guide.md)
- [API Documentation](docs/api-documentation.md)

## Contributing

We welcome contributions to DataRound Link! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Support

For support, please contact yuehan124@gmail.com or open an issue in the GitHub repository.

## Acknowledgments

Thanks to all contributors who have helped make DataRound Link better! 