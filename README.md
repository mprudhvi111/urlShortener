# URL Shortener

A Spring Boot application that allows users to create, manage, and track shortened URLs with authentication, pagination, and expiry features.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Database](#database)
- [API Endpoints](#api-endpoints)
- [Docker Support](#docker-support)
- [Contributing](#contributing)

## ✨ Features

- **User Authentication**: Secure user registration and login with Spring Security
- **URL Shortening**: Convert long URLs into short, shareable links
- **URL Management**: View, edit, delete, and manage all created URLs
- **URL Analytics**: Track click counts and expiry dates for shortened URLs
- **Pagination**: Browse URLs with efficient pagination support
- **Privacy Control**: Mark URLs as private or public
- **URL Expiry**: Set expiration dates for temporary shortened URLs
- **Admin Dashboard**: Administrative interface to manage users and URLs
- **PostgreSQL Database**: Reliable data persistence with PostgreSQL
- **Docker Support**: Easy deployment with Docker Compose
- **Flyway Migrations**: Database version control and migrations
- **Form Validation**: Server-side validation for data integrity

## 🛠️ Technology Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 4.0.0 |
| **Language** | Java 21 |
| **Database** | PostgreSQL 17 |
| **ORM** | Spring Data JPA / Hibernate |
| **UI Template** | Thymeleaf |
| **CSS Framework** | Bootstrap 5.3.8 |
| **Build Tool** | Maven |
| **Security** | Spring Security |
| **Validation** | Spring Validation |
| **Database Migration** | Flyway |
| **Container** | Docker & Docker Compose |

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for containerized setup)
- **PostgreSQL 17** (if running without Docker)

## 🚀 Installation

### Option 1: With Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd urlShortener
   ```

2. **Start the application with Docker Compose**
   ```bash
   docker-compose up -d
   ```

   This will start:
   - PostgreSQL database on port 5432
   - Spring Boot application on port 8109

3. **Access the application**
   - Open your browser and navigate to: `http://localhost:8109`

### Option 2: Without Docker (Local PostgreSQL)

1. **Ensure PostgreSQL is running**
   - Database: `postgres`
   - Username: `postgres`
   - Password: `postgres`
   - Port: `5432`

2. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Access the application**
   - Navigate to: `http://localhost:8109`

## ⚙️ Configuration

The application is configured via `src/main/resources/application.properties`:

```properties
# Server Configuration
spring.application.name=urlShortener
server.port=8109

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate Configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Kolkata

# Timezone
spring.jpa.show-sql=true

# Application Settings
app.base-url=http://localhost:8109
app.default-expiry-in-days=30
app.validate-url=true
app.page-size=10
```

### Key Configuration Options

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8109 |
| `app.base-url` | Base URL for shortened links | http://localhost:8109 |
| `app.default-expiry-in-days` | Default expiry period in days | 30 |
| `app.validate-url` | Enable URL validation | true |
| `app.page-size` | Number of items per page | 10 |

## 📖 Usage

### User Registration & Login

1. Click on **"Register"** to create a new account
2. Enter your email, name, and password
3. Click **"Login"** with your credentials

### Creating a Shortened URL

1. Login to your account
2. Enter the long URL in the input field
3. (Optional) Set privacy and expiry date
4. Click **"Shorten"**
5. Copy the generated short URL and share it

### Managing Your URLs

1. Navigate to **"My URLs"** page
2. View all your shortened URLs with statistics:
   - Click count
   - Creation date
   - Expiry date
   - Privacy status
3. Edit or delete URLs as needed
4. Use pagination to browse through your URLs

### Admin Functions

1. Login as an admin user
2. Access **"Admin Dashboard"**
3. View and manage all users and URLs in the system

## 📁 Project Structure

```
urlShortener/
├── src/
│   ├── main/
│   │   ├── java/com/urlShortener/urlShortener/
│   │   │   ├── controller/           # REST & MVC Controllers
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   └── UserController.java
│   │   │   ├── service/              # Business Logic
│   │   │   ├── repositories/         # Data Access Layer
│   │   │   ├── entities/             # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   └── ShortUrl.java
│   │   │   ├── dtos/                 # Data Transfer Objects
│   │   │   ├── exceptions/           # Custom Exceptions
│   │   │   ├── config/               # Configuration Classes
│   │   │   ├── ApplicationProperties.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── UrlShortenerApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       ├── data.sql
│   │       ├── templates/            # Thymeleaf Templates
│   │       │   ├── index.html
│   │       │   ├── login.html
│   │       │   ├── register.html
│   │       │   ├── my-urls.html
│   │       │   ├── admin-dashboard.html
│   │       │   ├── about.html
│   │       │   ├── layout.html
│   │       │   └── errors/
│   │       ├── static/               # Static Resources
│   │       │   └── css/
│   │       │       └── styles.css
│   │       └── db/migration/         # Flyway Migrations
│   │           ├── V1__createTables.sql
│   │           ├── V2__fillData.sql
│   │           └── V3__Update_Users_Password.sql
│   └── test/                         # Unit Tests
├── docker/
│   └── compose.yaml
├── compose.yaml                      # Docker Compose Configuration
├── pom.xml                           # Maven Dependencies
├── mvnw / mvnw.cmd                   # Maven Wrapper
└── README.md
```

## 🗄️ Database

### Schema Overview

#### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### Short URLs Table
```sql
CREATE TABLE short_urls (
    id BIGSERIAL PRIMARY KEY,
    short_key VARCHAR(10) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP,
    created_by BIGINT,
    click_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_short_urls_users 
        FOREIGN KEY (created_by) REFERENCES users (id)
);
```

### Database Migrations

Flyway manages database schema evolution:

- **V1__createTables.sql** - Creates users and short_urls tables
- **V2__fillData.sql** - Populates initial data
- **V3__Update_Users_Password.sql** - Updates password structure

## 🔌 API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/register` | Show registration form |
| POST | `/register` | Register new user |
| GET | `/login` | Show login form |
| POST | `/login` | Authenticate user |
| GET | `/logout` | Logout user |

### Home & Navigation

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Home page |
| GET | `/about` | About page |

### User URLs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/my-urls` | List user's URLs (paginated) |
| GET | `/my-urls?page={page}` | Browse user URLs by page |
| POST | `/shorten` | Create shortened URL |
| GET | `/edit/{id}` | Show edit form |
| POST | `/update/{id}` | Update shortened URL |
| DELETE | `/delete/{id}` | Delete shortened URL |

### Public Access

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/{shortKey}` | Redirect to original URL |

### Admin

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/dashboard` | Admin dashboard |
| GET | `/admin/users` | Manage users |
| GET | `/admin/urls` | Manage all URLs |

## 🐳 Docker Support

### Docker Compose Services

The `compose.yaml` file includes:

```yaml
services:
  postgres:
    image: 'postgres:17'
    environment:
      - POSTGRES_DB=postgres
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - '5432:5432'
```

### Building Docker Image

```bash
mvn spring-boot:build-image
```

### Running with Docker Compose

```bash
docker-compose up -d          # Start services
docker-compose logs -f        # View logs
docker-compose down           # Stop services
docker-compose restart        # Restart services
```

## 🐛 Troubleshooting

### Common Issues

**1. Port Already in Use**
```bash
# Change port in application.properties
server.port=8110
```

**2. Database Connection Failed**
- Ensure PostgreSQL is running on port 5432
- Verify credentials in `application.properties`
- Check Docker container logs: `docker-compose logs postgres`

**3. Ctrl+C Not Working in IntelliJ**
- Use `Ctrl+F2` to stop the application
- Or use the stop button in the Run toolbar
- Check Edit → Find → Find in Files for alternative shortcuts

**4. Pagination Not Working**
- Ensure `app.page-size` is set correctly in `application.properties`
- Verify page parameter is being passed: `?page=1`
- Check the page number is zero-indexed in Spring Data

## 📝 Building and Deployment

### Build the Project

```bash
mvn clean package
```

### Run Tests

```bash
mvn test
```

### Create Executable JAR

```bash
mvn clean install
# JAR will be in target/url-shortener-0.0.1-SNAPSHOT.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

Developed as a Spring Boot learning project.

## 📞 Support

For issues, questions, or suggestions, please create an issue in the repository.

---

**Last Updated**: May 10, 2026  
**Version**: 0.0.1-SNAPSHOT  
**Java Version**: 21  
**Spring Boot Version**: 4.0.0

