# Todo Datastar

This is a demo application showcasing the use of the Datastar Clojure SDK to create a simple Todo List application. The app demonstrates how to integrate Datastar's features for building dynamic, real-time web applications with Clojure.

## Features
- **Add Todos**: Create new todo items with a title and description.
- **List Todos**: View all existing todo items in a dynamic list.
- **Mark as Done**: Mark a todo item as completed.
- **Delete Todos**: Remove a todo item from the list.
- **Real-Time Updates**: Uses Datastar's Server-Sent Events (SSE) for real-time updates to the UI.

## Technologies Used
- **Clojure**: Backend logic and server-side functionality.
- **Datastar SDK**: For real-time updates and dynamic UI interactions.
- **PostgreSQL**: Database for storing todo items.
- **Docker**: Containerized environment for the database and application.
- **Hiccup**: HTML generation in Clojure.
- **Reitit**: Routing library for handling HTTP requests.
- **Flyway**: Database migration tool.

## How It Works
1. **Database Setup**: The app uses PostgreSQL to store todo items. The database schema is managed using Flyway migrations.
2. **Dynamic UI**: The frontend dynamically updates using Datastar's SSE capabilities.
3. **REST API**: The app exposes endpoints for creating, listing, updating, and deleting todos.
4. **Dockerized Environment**: Both the app and the database run in Docker containers for easy setup and deployment.

## Getting Started
### Prerequisites
- Docker and Docker Compose installed.
- Java and Clojure installed locally.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/koloyyee/todo-datastar.git
   cd todo-datastar
   ```
2. Start the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```
3. Get your REPL runnning and eval `(start-sys)` or `(restart-sys)` in `main.clj`
4. Access the app in your browser at [http://localhost:8080](http://localhost:8080).

<!--### Database Migration
The database schema is managed using Flyway. To apply migrations:
```bash
docker exec -it <app-container-name> bash
clojure -X:main migrate-db!
```-->

## File Structure
- `src/todo_datastar/`: Contains the Clojure source code.
- `resources/db/migration/`: SQL migration files for the database.
- `docker-compose.yml`: Docker configuration for the app and database.

## License
This project is licensed under the MIT License.
