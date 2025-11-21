-- Database: datomic

 DROP DATABASE todo_datastar;


CREATE DATABASE todo_datastar
 WITH OWNER = postgres 
      TEMPLATE template0
      ENCODING = 'UTF8'
      TABLESPACE = pg_default
      LC_COLLATE = 'en_US.UTF-8'
      LC_CTYPE = 'en_US.UTF-8'
      CONNECTION LIMIT = -1;
