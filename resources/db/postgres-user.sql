
-- DROP ROLE :datastar
CREATE ROLE datastar LOGIN PASSWORD 'postgres';


GRANT CREATE, USAGE ON SCHEMA public TO datastar;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO datastar;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO datastar;

-- Also grant default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO datastar;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO datastar;


ALTER DATABASE todo_datastar OWNER TO datastar;
--ALTER TABLE todos OWNER TO datastar;
--ALTER TABLE datomic_kvs OWNER TO datastar;