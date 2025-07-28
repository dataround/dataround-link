# DataRound Link Web

DataRound Link Web is the frontend application for DataRound Link, a data integration and synchronization platform. It provides a user-friendly interface for managing database connections, jobs, and data synchronization tasks.

## Features

- Database connection management
- Job configuration and scheduling
- Real-time job monitoring
- Virtual table management
- Multi-language support (English/Chinese)

## Prerequisites

- Node.js (v16 or higher)
- Yarn package manager
- Modern web browser (Chrome, Firefox, Safari, Edge)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/dataround/dataround-link.git
cd dataround-link/dataround-link-web
```

2. Install dependencies:
```bash
yarn install
```

## Development

To start the development server:

```bash
yarn start
```

The application will be available at `http://localhost:5173/datalink`

## Building for Production

To build the application for production:

```bash
yarn run build
```

The build artifacts will be stored in the `../mydp-datalink-svc/src/main/resources/static` directory.

## Project Structure

```
src/
├── api/          # API service calls
├── components/   # Reusable UI components
├── hooks/        # Custom React hooks
├── layout/       # Layout components
├── pages/        # Page components
├── router/       # Routing configuration
├── store/        # State management
├── utils/        # Utility functions
└── locales/      # Internationalization files
```

## License

This project is licensed under the GNU General Public License v3.0 - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For support, please contact yuehan124@gmail.com