Backend:
cd app-api
.\mvnw spring-boot:run

Frontend:
cd app-client
npm install
npm run dev

API:
GET /api/trips
GET /api/trips?sort=price
GET /api/trips?maxPrice=1200
GET /api/trips/{code}