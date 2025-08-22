# OAuth 2.0 / OIDC Authentication Service

A comprehensive Quarkus-based authentication service implementing OAuth 2.0 and OpenID Connect (OIDC) flows for Google, GitHub, and Facebook authentication.

## 🚀 Features

- **Multiple OAuth Providers**: Support for Google (OIDC), GitHub, and Facebook
- **JWT Token Management**: Secure JWT generation and validation
- **User Management**: Automatic user creation and profile management
- **CORS Support**: Configured for frontend integration
- **Comprehensive Testing**: Unit, integration, and security tests
- **Database Integration**: PostgreSQL with Hibernate ORM

## 🏗️ Architecture

### Authentication Flow

1. **Frontend Initiation**: User clicks "Sign in with [Provider]"
2. **OAuth Redirect**: Backend redirects to provider's authentication page
3. **User Authentication**: User authenticates with the provider
4. **Authorization Code**: Provider redirects back with authorization code
5. **Token Exchange**: Backend exchanges code for access token
6. **User Profile**: Backend fetches user profile from provider
7. **User Management**: Create/update user in local database
8. **JWT Generation**: Generate application-specific JWT
9. **Frontend Response**: Return JWT to frontend for authenticated requests

### Supported Providers

- **Google**: OpenID Connect (OIDC) implementation
- **GitHub**: OAuth 2.0 with user profile API
- **Facebook**: OAuth 2.0 with Graph API

## 🛠️ Setup

### Prerequisites

- Java 21+
- PostgreSQL 12+
- OAuth application credentials from providers

### Environment Variables

Create a `.env` file or set these environment variables:

```bash
# Database
DB_USERNAME=auth_user
DB_PASSWORD=auth_password
DB_URL=postgresql://localhost:5432/auth_db

# Google OAuth
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# GitHub OAuth
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
GITHUB_REDIRECT_URI=http://localhost:8080/auth/github/callback

# Facebook OAuth
FACEBOOK_CLIENT_ID=your-facebook-client-id
FACEBOOK_CLIENT_SECRET=your-facebook-client-secret
FACEBOOK_REDIRECT_URI=http://localhost:8080/auth/facebook/callback

# Application
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRATION=86400
FRONTEND_URL=http://localhost:3000
```

### Database Setup

```sql
CREATE DATABASE auth_db;
CREATE USER auth_user WITH PASSWORD 'auth_password';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
```

### OAuth Provider Setup

#### Google
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized redirect URIs: `http://localhost:8080/auth/google/callback`

#### GitHub
1. Go to GitHub Settings > Developer settings > OAuth Apps
2. Create a new OAuth App
3. Set Authorization callback URL: `http://localhost:8080/auth/github/callback`

#### Facebook
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app
3. Add Facebook Login product
4. Set Valid OAuth Redirect URIs: `http://localhost:8080/auth/facebook/callback`

## 🏃‍♂️ Running the Application

### Development Mode

```bash
./gradlew quarkusDev
```

### Production Mode

```bash
./gradlew build
java -jar build/quarkus-app/quarkus-run.jar
```

## 📚 API Endpoints

### Public Endpoints

- `GET /hello` - Public greeting
- `GET /auth/github` - Initiate GitHub OAuth
- `GET /auth/facebook` - Initiate Facebook OAuth
- `GET /auth/github/callback` - GitHub OAuth callback
- `GET /auth/facebook/callback` - Facebook OAuth callback
- `POST /auth/google` - Google OIDC authentication

### Protected Endpoints (Require JWT)

- `GET /hello/secure` - Protected greeting
- `GET /auth/me` - Get current user profile
- `POST /auth/logout` - Logout (client-side token removal)

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Test Categories

- **Unit Tests**: Service layer testing
- **Integration Tests**: API endpoint testing
- **Security Tests**: Authentication and authorization
- **OAuth Flow Tests**: End-to-end authentication flows

### Example Test Scenarios

1. **OAuth Initiation**: Tests proper redirect URL generation
2. **Callback Handling**: Tests code exchange and error handling
3. **User Management**: Tests user creation and updates
4. **JWT Generation**: Tests token creation and validation
5. **Security**: Tests protected endpoint access

## 🔧 Frontend Integration

### JavaScript Example

```javascript
// Initiate GitHub authentication
window.location.href = 'http://localhost:8080/auth/github?state=random-state';

// Handle successful authentication (redirect to /auth/success?token=...)
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');
if (token) {
    localStorage.setItem('authToken', token);
}

// Make authenticated requests
fetch('http://localhost:8080/auth/me', {
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
})
.then(response => response.json())
.then(user => console.log('Current user:', user));
```

### React Example

```jsx
const authService = {
    loginWithGitHub: () => {
        const state = Math.random().toString(36);
        localStorage.setItem('oauthState', state);
        window.location.href = `http://localhost:8080/auth/github?state=${state}`;
    },
    
    getCurrentUser: async () => {
        const token = localStorage.getItem('authToken');
        if (!token) return null;
        
        const response = await fetch('http://localhost:8080/auth/me', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        return response.ok ? response.json() : null;
    }
};
```

## 🔒 Security Considerations

1. **JWT Secrets**: Use strong, unique secrets in production
2. **HTTPS**: Always use HTTPS in production
3. **Token Expiration**: Configure appropriate token lifetimes
4. **CORS**: Restrict CORS origins to your frontend domains
5. **Rate Limiting**: Implement rate limiting for auth endpoints
6. **State Parameter**: Always validate OAuth state parameter

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/bytebrain/
│   │   ├── client/          # REST clients for OAuth providers
│   │   ├── dto/auth/        # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── resource/        # REST endpoints
│   │   └── service/         # Business logic
│   └── resources/
│       ├── META-INF/resources/ # JWT keys
│       └── application.properties
└── test/
    ├── java/com/bytebrain/
    │   ├── integration/     # Integration tests
    │   ├── resource/        # REST endpoint tests
    │   └── service/         # Service layer tests
```

## 🚀 Deployment

### Docker

```dockerfile
FROM registry.access.redhat.com/ubi8/openjdk-21:1.18
COPY build/quarkus-app/ /deployments/
EXPOSE 8080
USER 185
ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
```

### Build Native Image

```bash
./gradlew build -Dquarkus.package.type=native
```

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📞 Support

For questions and support, please open an issue in the GitHub repository.
