# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run tests excluding specific tags (e.g., performance tests)
./gradlew test -PexcludeTags=performance

# Clean build
./gradlew clean build

# Generate documentation (includes Spring REST Docs)
./gradlew asciidoctor
```

### Database
- Uses H2 database by default (TCP mode: `jdbc:h2:tcp://localhost/~/kkori`)
- MySQL connector also included for production use
- JPA with Hibernate DDL auto mode is set to `create` (recreates schema on startup)
- Test profile uses in-memory H2

## Architecture Overview

### Core Domain Architecture
This is an interview practice application with two main domains:

1. **Question Set Management**: Users can create, version, and manage question sets with expected answers
2. **Real-time Interview Sessions**: WebSocket-based interview rooms with STT/TTS integration

### Key Architectural Patterns

#### Question Set Versioning System
- Question sets support versioning with parent-child relationships
- Each version can contain completely new questions OR existing questions with modified answers
- Versioning is handled via `QuestionSet.parentVersionId` and `versionNumber` fields
- The mapping between question sets and questions is handled through `QuestionSetQuestionMap` entity

#### Interview Session Management
- Real-time interview sessions use WebSocket with STOMP protocol
- `InterviewRoomManager` component handles room lifecycle and user permissions
- Sessions support both predefined question sets and custom questions
- Integrates with GMS (SSAFY's GPT/Whisper proxy) for AI-powered features

### Database Design
- **Soft delete pattern**: Most entities use `isDeleted` flag instead of hard deletes
- **Optimized indexing**: Heavy indexing on frequently queried fields (see QuestionSet entity indexes)
- **JPA Auditing**: `BaseEntity` provides automatic `createdAt`/`updatedAt` timestamps

### Security & Authentication
- JWT-based authentication with refresh tokens
- Kakao OAuth2 integration
- Custom `@LoginUser` annotation extracts current user from security context
- Security aspects for audit logging (`SecurityAuditAspect`)

### Request/Response Structure
- All API responses wrapped in `CommonApiResponse<T>` for consistent format
- Request DTOs use Jakarta validation with custom error handling
- Extensive use of Builder pattern for immutable DTOs

### External Service Integration
- **GMS API**: SSAFY's proxy for OpenAI services (GPT, Whisper)
- **WebRTC**: Signaling server for video/audio communication
- **WebSocket**: Real-time communication for interview sessions

### Key Components to Understand

#### QuestionSet Domain
- `QuestionSetService`: Core business logic for question set CRUD and versioning
- Supports three main operations: create new set, create version with new Q&A, create version with answer modifications  
- **Immutability principle**: Question entities are never modified, new versions create new Question instances
- **Version management**: Parent-child relationships tracked through `parentVersionId` with automatic version numbering
- **Access control**: Owner-based permissions with public/private visibility settings
- Tags are managed separately through `QuestionSetTag` and `Tag` entities

#### Interview Domain
- `InterviewSession`: Represents an active interview with state management
- `InterviewRoom`: WebSocket room container with user roles and permissions
- `TailQuestionGenerator`: AI-powered follow-up question generation

#### Security Context
- `CustomUserDetails` for Spring Security integration
- `LoginUserArgumentResolver` for automatic user injection in controllers
- `SecurityContextValidator` ensures proper authentication state

## Testing
- Tests use `@SpringBootTest` for integration testing
- Spring REST Docs configured for API documentation generation
- Test classes follow naming convention: `*Test.java` for unit tests, `*CrudTest.java` for integration tests
- Test exclusion tags supported: use `-PexcludeTags=performance` to skip performance tests
- JUnit Platform Launcher configured for test execution

## Configuration Notes
- Application uses multiple profiles: default, test, secret
- GMS API configuration required for AI features
- WebSocket configuration in `WebSocketConfig` for interview functionality
- CORS configured in `WebConfig` for frontend integration

## Performance Considerations
- Database indexes optimized for common query patterns
- Lazy loading configured for JPA relationships
- Concurrent access managed through `ConcurrencyControlService`
- Interview room capacity limits enforced at application level

## Important Implementation Details

### Transaction Management
- Service methods use `@Transactional` with `READ_COMMITTED` isolation level
- Custom timeout settings per operation type (30s for complex operations, 15s for updates)
- Rollback configured for all exceptions to ensure data consistency

### Question Set API Request Structure
- **Critical**: For version creation APIs (`/versions/with-new-qa`, `/versions/with-answer-modifications`), the request body MUST include `parentQuestionSetId` even though it's also in the URL path
- This is due to validation occurring before the controller sets the PathVariable value
- Example: `POST /api/questionsets/1/versions/with-new-qa` requires `"parentQuestionSetId": 1` in request body

### Entity Relationships and Immutability
- `Question` entities store `expectedAnswer` directly (no separate Answer entity for expected answers)
- `QuestionSetQuestionMap` handles the many-to-many relationship with ordering
- Version creation never modifies existing Question instances, always creates new ones
- `QuestionSet.canBeAccessedBy()` and `isOwner()` methods handle access control logic