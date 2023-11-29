CREATE USER "app_user" WITH PASSWORD 'app_password';
GRANT ALL ON SCHEMA public TO "app_user";
GRANT CONNECT ON DATABASE "zeiterfassung" TO "app_user";
ALTER DEFAULT PRIVILEGES IN SCHEMA "public" GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON TABLES TO "app_user";
ALTER DEFAULT PRIVILEGES IN SCHEMA "public" GRANT USAGE ON SEQUENCES TO "app_user";
ALTER DEFAULT PRIVILEGES IN SCHEMA "public" GRANT EXECUTE ON FUNCTIONS TO "app_user";
