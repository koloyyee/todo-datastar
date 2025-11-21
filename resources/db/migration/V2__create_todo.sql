-- V1__create_todos.sql
CREATE TABLE todos (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT,
  done BOOLEAN DEFAULT FALSE
);
ALTER TABLE todos
 OWNER TO datastar;
GRANT ALL ON TABLE todos TO datastar;
GRANT ALL ON TABLE todos TO public;