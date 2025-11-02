-- V1__create_todos.sql
CREATE TABLE todos (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT,
  done BOOLEAN DEFAULT FALSE
);