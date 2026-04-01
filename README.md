# Financial Budget Tracker

A full-stack 12-month Financial Budget Tracker web application built with Spring Boot and React. Users can sign up, log in, and manage a forward-looking cash flow plan across a calendar year — tracking inflows and outflows month by month, with automatic totals.

## Tech Stack

### Backend
- **Java 17** with Spring Boot 3.x
- **Spring Security** with JWT (stateless authentication)
- **Spring Data JPA** with Hibernate
- **PostgreSQL** database
- **Maven** build system
- **Docker** for deployment

### Frontend
- **React 18** with Vite
- **Tailwind CSS** for styling
- **React Router v6** for routing
- **Axios** for HTTP requests
- **React Query** for state management

### Deployment
- **Render** for backend + database
- **Vercel** for frontend

## Features

- User authentication with JWT tokens
- Create and manage multiple budget plans (one per year)
- Automatic seeding of default budget items
- Spreadsheet-like budget table with 12-month view
- Real-time calculations and updates
- Nigerian Naira (₦) currency formatting
- Responsive design for desktop and mobile
- Ownership-based access control

## Project Structure

```
/
├── backend/          ← Spring Boot project (Maven)
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/budgettracker/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── dto/
│   │               ├── entity/
│   │               ├── repository/
│   │               ├── security/
│   │               └── service/
│   ├── src/
│   │   └── main/
│   │       └── resources/
│   │           ├── application.properties
│   │           ├── application-dev.properties
│   │           └── application-prod.properties
│   ├── pom.xml
│   └── Dockerfile
├── frontend/         ← Vite + React project
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── utils/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   └── vercel.json
└── README.md
```

## Database Schema

### Users Table
- `id` - BIGSERIAL PRIMARY KEY
- `email` - VARCHAR(255) UNIQUE NOT NULL
- `password` - VARCHAR(255) NOT NULL (BCrypt hashed)
- `full_name` - VARCHAR(255) NOT NULL
- `created_at` - TIMESTAMP DEFAULT NOW()

### Budget Plans Table
- `id` - BIGSERIAL PRIMARY KEY
- `user_id` - BIGINT REFERENCES users(id) ON DELETE CASCADE
- `year` - INTEGER NOT NULL
- `name` - VARCHAR(255) NOT NULL
- `created_at` - TIMESTAMP DEFAULT NOW()
- UNIQUE(user_id, year)

### Budget Items Table
- `id` - BIGSERIAL PRIMARY KEY
- `budget_plan_id` - BIGINT REFERENCES budget_plans(id) ON DELETE CASCADE
- `name` - VARCHAR(255) NOT NULL
- `group_type` - VARCHAR(50) NOT NULL (INFLOW | FIXED_EXPENSE | VARIABLE_COST)
- `sort_order` - INTEGER NOT NULL DEFAULT 0

### Monthly Values Table
- `id` - BIGSERIAL PRIMARY KEY
- `budget_item_id` - BIGINT REFERENCES budget_items(id) ON DELETE CASCADE
- `month` - INTEGER NOT NULL (1 = January ... 12 = December)
- `amount` - NUMERIC(15, 2) NOT NULL DEFAULT 0
- UNIQUE(budget_item_id, month)

## Local Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 16+
- PostgreSQL 12+
- npm or yarn

### Backend Setup

1. **Create PostgreSQL database:**
   ```sql
   CREATE DATABASE budget_dev;
   ```

2. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:5173`

### Environment Variables

#### Backend Environment Variables
- `DATABASE_URL` - PostgreSQL connection URL (for production)
- `DB_USERNAME` - Database username (for production)
- `DB_PASSWORD` - Database password (for production)
- `JWT_SECRET` - Secret key for JWT token signing
- `SPRING_PROFILES_ACTIVE` - Spring profile (dev/prod)
- `ALLOWED_ORIGINS` - Comma-separated list of allowed frontend origins

#### Frontend Environment Variables
- `VITE_API_URL` - Backend API URL (e.g., `https://your-backend.onrender.com/api`)

## Default Budget Items

When a new budget plan is created, the following default items are automatically seeded:

### Inflows
- Salary
- Other Income

### Fixed Expenses
- Rent
- Data
- Emergency Fund
- Savings
- Miscellaneous
- Electricity

### Variable Costs
- Feeding
- Transportation
- Loan

All items start with ₦0.00 values for all 12 months.

## API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Budget Plans (`/api/budget-plans`)
- `GET /api/budget-plans` - List all plans for authenticated user
- `POST /api/budget-plans` - Create new budget plan
- `GET /api/budget-plans/{id}` - Get budget plan details
- `DELETE /api/budget-plans/{id}` - Delete budget plan

### Budget Items (`/api/budget-plans/{planId}/items`)
- `POST /api/budget-plans/{planId}/items` - Create new budget item
- `PUT /api/budget-plans/{planId}/items/{itemId}` - Update budget item
- `DELETE /api/budget-plans/{planId}/items/{itemId}` - Delete budget item
- `PATCH /api/budget-plans/{planId}/items/{itemId}/monthly-values` - Update monthly value

### Budget Summary
- `GET /api/budget-plans/{id}/summary` - Get complete budget summary with calculated totals

## Deployment

### Backend Deployment (Render)

1. **Create PostgreSQL Database on Render:**
   - Go to Render Dashboard → New → PostgreSQL
   - Choose a name and region
   - Note the database connection details

2. **Create Backend Web Service:**
   - Go to Render Dashboard → New → Web Service
   - Connect your GitHub repository
   - Set root directory to `backend`
   - Use the provided Dockerfile
   - Add environment variables:
     ```
     DATABASE_URL=<from Render PostgreSQL>
     DB_USERNAME=<from Render PostgreSQL>
     DB_PASSWORD=<from Render PostgreSQL>
     JWT_SECRET=<generate a random 64-char string>
     SPRING_PROFILES_ACTIVE=prod
     ALLOWED_ORIGINS=https://<your-vercel-app>.vercel.app
     ```

### Frontend Deployment (Vercel)

1. **Deploy to Vercel:**
   - Go to Vercel Dashboard → New Project
   - Connect your GitHub repository
   - Set root directory to `frontend`
   - Build command: `npm run build`
   - Output directory: `dist`
   - Add environment variable:
     ```
     VITE_API_URL=https://<your-render-backend>.onrender.com/api
     ```

## Security Features

- JWT-based stateless authentication
- BCrypt password hashing
- CORS configuration for frontend origins
- Ownership validation for all budget operations
- Input validation and sanitization
- SQL injection prevention via JPA

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

If you encounter any issues or have questions, please open an issue on the GitHub repository.
# BudgetTracker
