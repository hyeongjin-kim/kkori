# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 application built with Java 21 for an interview question management system called "Kkori". The system manages question sets, versions, user authentication, and real-time interview sessions.

## Development Commands

### Build and Test
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run the application
./gradlew bootRun

# Clean build
./gradlew clean build
```

### Database
- Uses H2 in-memory database for development
- JPA/Hibernate with Spring Data repositories

### Key Technologies
- Spring Boot 3.5.3 with Java 21
- Spring Security with JWT authentication
- WebSocket for real-time communication
- Spring Data JPA with H2 database
- Lombok for boilerplate reduction
- Spring REST Docs for API documentation

## Architecture Overview

### Core Domain Entities
- **QuestionSet**: Main aggregate root for question collections with versioning support
- **Question**: Immutable question entities (content, type)
- **Answer**: User-generated answers linked to questions
- **QuestionSetQuestionMap**: Junction entity linking questions to sets with display order
- **TailQuestion**: Follow-up questions generated during interviews
- **User**: Authentication and ownership management

### Service Layer Architecture
- **QuestionSetService**: Main business logic for CRUD operations on question sets
- **InterviewSessionService**: Manages real-time interview sessions
- **KakaoOAuth2Service**: Handles OAuth2 authentication with Kakao

### Key Design Patterns
1. **Immutable Entity Design**: Questions and core data are immutable for consistency
2. **Version Management**: QuestionSets support parent-child versioning relationships
3. **Soft Delete Pattern**: Entities use logical deletion rather than physical removal
4. **Repository Pattern**: Spring Data JPA repositories with custom query methods

### Controller Organization
- **QuestionSetController**: CRUD operations for question sets
- **InterviewController**: Real-time interview session management
- **WebRTCSignalingController**: WebSocket signaling for video calls
- **KakaoOAuth2Controller**: OAuth2 authentication endpoints

### Important Business Logic
- Question sets can be copied, versioned, and shared between users
- Version control allows tracking changes and maintaining parent-child relationships  
- Answers are user-specific and tied to question-set mappings
- Interview sessions support real-time question delivery and tail question generation

### Security & Authentication
- JWT-based authentication with Kakao OAuth2 integration
- Custom `@LoginUser` annotation for user context injection
- Role-based access control for question set ownership

### Testing Strategy
- Unit tests for service layer business logic
- Integration tests for repository operations
- WebSocket integration tests for real-time features
- Security tests for authentication flows