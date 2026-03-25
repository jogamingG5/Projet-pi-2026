# Street League - Sports Management System

A comprehensive Spring Boot REST API for managing sports events, teams, matches, players, and more. Built with Java 17, Spring Boot 4.0.4, and MongoDB.

## 🎯 Overview

Street League is a full-featured sports management platform that enables organizations to manage:
- **Teams & Players** - Registration, profiles, and team management
- **Matches & Events** - Schedule, track, and manage sports events
- **Referees & Arbiters** - Assign arbiters to matches
- **Partnerships** - Sponsor and municipality partnerships
- **Statistics & Rankings** - Track team performance and player statistics
- **Match Sheets** - Record match details, scores, and disciplinary actions

## ✨ Features

- 📋 Complete user management system with role-based access (Players, Coaches, Arbiters, Sponsors)
- ⚽ Sports and team management
- 🏆 Event and tournament organization
- 📊 Statistics and ranking calculations
- 🏟️ Field/terrain reservation and management
- 📝 Match sheet reporting and documentation
- 👥 Coach and player availability tracking
- 💼 Sponsorship and partnership management
- 🔔 Notifications system
- 🛡️ Request logging middleware for security and debugging

## 🛠️ Tech Stack

- **Runtime**: Java 17
- **Framework**: Spring Boot 4.0.4
- **Build Tool**: Maven
- **Database**: MongoDB
- **Architecture**: RESTful API with MVC pattern

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- MongoDB (local or remote connection)
- Git

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Daly-belguith/Projet-pi-2026.git
   cd Projet-pi-2026
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```
   
   On Windows:
   ```bash
   mvnw.cmd clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   On Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

The application will start on `http://localhost:8080/streetleague`

## ⚙️ Configuration

### Application Properties

Update `src/main/resources/application.properties` to customize:

```properties
# Application name
spring.application.name=projectPi

# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/
spring.data.mongodb.database=projectPi

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Server Configuration
server.port=8080
server.servlet.context-path=/streetleague
```

## 📁 Project Structure

```
src/main/java/com/example/projectPi/
├── controllers/          # REST endpoints for all entities
│   ├── HelloController.java
│   └── UserController.java
├── services/            # Business logic layer
│   └── UserService.java
├── repositories/        # Data access layer (Spring Data MongoDB)
│   └── UserRepository.java
├── models/              # Data models and entities
│   ├── User.java
│   └── UserType.java
├── middlewares/         # Filters and interceptors
│   └── LoggingFilter.java
└── ProjectPiApplication.java  # Main application class

src/main/resources/
├── application.properties  # Application configuration
```

## 🗄️ Database Schema

The application uses MongoDB with the following main collections:

### Users & Roles
- **user** - Base user account with authentication
- **arbitre** - Referee/arbiter with certifications and experience
- **coach** - Coach with team assignment
- **joueur** - Player with team and position information

### Sports & Events
- **sport** - Sport type with rules and constraints
- **event** - Sporting events and tournaments
- **match** - Individual matches between teams
- **feuillesDeMatch** - Match sheets with scores and disciplinary records

### Team Management
- **equipe** - Team information with roster
- **statistiques** - Team and player statistics
- **terrains** - Sports fields with capacity and location

### Business
- **sponsorships** - Sponsor information
- **partenariat** - Partnership agreements between sponsors and municipalities
- **publicite** - Advertising campaigns
- **municipalite** - Municipality information

### Rules & Governance
- **Réglement** - Sport-specific rules and regulations

## 🔌 API Endpoints

### Users
- `GET /api/users` - List all users
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Additional endpoints available for:
- Teams (`/api/equipes`)
- Matches (`/api/matches`)
- Events (`/api/events`)
- Players (`/api/joueurs`)
- And more...

For detailed API documentation, refer to the controller classes in `src/main/java/com/example/projectPi/controllers/`

## 🧪 Testing

Run tests with Maven:
```bash
./mvnw test
```

On Windows:
```bash
mvnw.cmd test
```

Test files are located in `src/test/java/`

## 🔐 Security

The application includes:
- Request logging middleware for audit trails
- User authentication and role-based access control
- Data validation at service layer

## 📝 Logging

The application uses Spring's built-in logging with a custom `LoggingFilter` middleware to:
- Log all incoming HTTP requests
- Track request processing
- Record response status and timing
- Help with debugging and monitoring

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Daly Belguith** - Project Developer

- **Youssef zaiene** - Project Developer

- **Yosra Omri** - Project Developer

- **Karim Troudi** - Project Developer



## 📧 Contact

For questions or issues, please open an issue in the GitHub repository.

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- MongoDB for reliable database services
- The open-source community for various libraries and tools

---

**Last Updated**: March 2026  
**Version**: 0.0.1-SNAPSHOT
