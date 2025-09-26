-- Postgresql schema for dataround_link --
-- Before starting Spring Boot, please create the database dataround_link first. --

CREATE TABLE IF NOT EXISTS public.connection (
    id BIGSERIAL PRIMARY KEY,
    project_id int8 NOT NULL,
    "name" varchar NOT NULL,
    connector varchar NOT NULL,
    host varchar NULL,
    port int4 NULL,
    "user" varchar NULL,
    passwd varchar NULL,
    config JSONB NOT NULL,
    description varchar NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS public.virtual_table (
    id BIGSERIAL PRIMARY KEY,
    connection_id int8 NOT NULL,
    project_id int8 NOT NULL,
    "database" varchar NOT NULL,
    table_name varchar NOT NULL,
    table_config varchar NOT NULL,
    description varchar NULL,
    deleted bool NOT NULL DEFAULT FALSE,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS public.virtual_field (
    id BIGSERIAL PRIMARY KEY,
    table_id int8 NOT NULL,
    "name" varchar NOT NULL,
    type varchar NOT NULL,
    comment varchar NULL,
    nullable bool NULL,
    primary_key bool NULL,
    default_value varchar NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS public.job (
    id BIGSERIAL PRIMARY KEY,
    project_id int8 NOT NULL,
    "name" varchar NOT NULL,
    description varchar NULL,
    job_type int2 NOT NULL,
    schedule_type int2 NOT NULL,
    cron varchar NULL,
    start_time timestamp with time zone NULL,
    end_time timestamp with time zone NULL,
    config jsonb NOT NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
COMMENT ON COLUMN public.job.job_type IS '1:batch 2:stream';
COMMENT ON COLUMN public.job.schedule_type IS '1:run_now 2:schedule 3:not_run';

CREATE TABLE IF NOT EXISTS public.job_instance (
    id BIGSERIAL PRIMARY KEY,
    job_id int8 NOT NULL,
    project_id int8 NOT NULL,
    status int2 NOT NULL,
    seatunnel_id varchar NULL,
    start_time timestamp with time zone NULL,
    end_time timestamp with time zone NULL,
    read_count int8 NULL,
    write_count int8 NULL,
    read_qps double precision NULL,
    write_qps double precision NULL,
    read_bytes int8 NULL,
    write_bytes int8 NULL,
    log_content text NULL,
    update_by int8 NOT NULL,
    update_time timestamp with time zone NOT NULL
);
COMMENT ON COLUMN public.job_instance.status IS '0:waiting, 1:submitted, 2:running, 3:success, 4:failure, 5:canceled';

CREATE TABLE IF NOT EXISTS public.connector (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    plugin_name VARCHAR(100) NOT NULL,
    support_source BOOLEAN NOT NULL DEFAULT FALSE,
    support_sink BOOLEAN NOT NULL DEFAULT FALSE,
    is_stream BOOLEAN NOT NULL DEFAULT FALSE,
    virtual_table BOOLEAN NOT NULL DEFAULT FALSE,
    properties JSONB NOT NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);

-- Insert database connectors
INSERT INTO public.connector (name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, properties, create_by, update_by, create_time, update_time) VALUES
('MySQL', 'Database', 'JDBC-MYSQL', true, true, false, false, '{"driver":"com.mysql.cj.jdbc.Driver","host":"localhost","port":3306,"url":"jdbc:mysql://localhost:3306/default?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"}', 1000, 1000, now(), now()),
('PostgreSQL', 'Database', 'JDBC-POSTGRES', true, true, false, false, '{"driver":"org.postgresql.Driver","host":"localhost","port":5432,"url":"jdbc:postgresql://localhost:5432/"}', 1000, 1000, now(), now()),
('Oracle', 'Database', 'JDBC-ORACLE', true, true, false, false, '{"driver":"oracle.jdbc.OracleDriver","host":"localhost","port":1521,"url":"jdbc:oracle:thin:@localhost:1521:ORCL"}', 1000, 1000, now(), now()),
('SQLServer', 'Database', 'JDBC-SQLSERVER', true, true, false, false, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","host":"localhost","port":1433,"url":"jdbc:sqlserver://localhost:1433;DatabaseName=seatunnel"}', 1000, 1000, now(), now()),
('Tidb', 'Database', 'JDBC-TIDB', true, true, false, false, '{"driver":"com.mysql.jdbc.Driver","host":"localhost","port":4000,"url":"jdbc:mysql://localhost:4000/seatunnel"}', 1000, 1000, now(), now()),
('Hive', 'Database', 'Hive', true, true, false, false, '{}', 10000, 10000, now(), now())
ON CONFLICT (name) DO NOTHING;

-- Insert CDC connectors
INSERT INTO public.connector (name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, properties, create_by, update_by, create_time, update_time) VALUES
('MySQL-CDC', 'Database', 'MYSQL-CDC', true, false, true, false, '{"driver":"com.mysql.jdbc.Driver","url":"jdbc:mysql://mysql:3306"}', 10000, 10000, now(), now()),
('SQLServer-CDC', 'Database', 'SQLServer-CDC', true, false, true, false, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","url":"jdbc:sqlserver://sqlserver:1433"}', 10000, 10000, now(), now())
ON CONFLICT (name) DO NOTHING;

-- Insert nonstructural connectors
INSERT INTO public.connector (name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, properties, create_by, update_by, create_time, update_time) VALUES
('Kafka', 'MQ', 'KAFKA', true, true, true, true, '{"bootstrap.servers":"localhost:9092","topics":"topic1"}', 10000, 10000, now(), now()),
('FTP', 'File', 'FTP', 't', 't', 'f', 'f', '{}', 10000, 10000, '2025-07-18 17:28:17', '2025-07-18 17:28:20'),
('S3', 'File', 'S3', 't', 't', 'f', 'f', '{}', 10000, 10000, '2025-07-20 21:14:22', '2025-07-20 21:14:25'),
('LocalFile', 'File', 'Local', 't', 't', 'f', 'f', '{}', 10000, 10000, '2025-07-22 10:59:37', '2025-07-22 10:59:40'),
('SFTP', 'File', 'SFTP', 't', 't', 'f', 'f', '{}', 10000, 10000, '2025-07-27 08:40:38', '2025-07-27 08:40:41')
ON CONFLICT (name) DO NOTHING;

ALTER SEQUENCE connection_id_seq RESTART WITH 10000;
ALTER SEQUENCE virtual_table_id_seq RESTART WITH 10000;
ALTER SEQUENCE virtual_field_id_seq RESTART WITH 10000;
ALTER SEQUENCE job_id_seq RESTART WITH 10000;
ALTER SEQUENCE job_instance_id_seq RESTART WITH 10000;
ALTER SEQUENCE connector_id_seq RESTART WITH 10000;
