-- Postgresql schema for dataround_link --
-- Before starting Spring Boot, please create the database dataround_link first. --

CREATE SEQUENCE IF NOT EXISTS connection_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.connection (
    id BIGINT PRIMARY KEY DEFAULT nextval('connection_id_seq'),
    project_id int8 NOT NULL,
    "name" varchar(255) NOT NULL,
    connector varchar(50) NOT NULL,
    connector_version_id int8 NULL,
    host varchar(255) NULL,
    port int4 NULL,
    "user" varchar(255) NULL,
    passwd varchar(255) NULL,
    config JSONB NOT NULL,
    description varchar(255) NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
ALTER SEQUENCE connection_id_seq OWNED BY connection.id;

CREATE SEQUENCE IF NOT EXISTS virtual_table_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.virtual_table (
    id BIGINT PRIMARY KEY DEFAULT nextval('virtual_table_id_seq'),
    connection_id int8 NOT NULL,
    project_id int8 NOT NULL,
    "database" varchar(255) NOT NULL,
    table_name varchar(255) NOT NULL,
    table_config varchar(1024) NOT NULL,
    description varchar(255) NULL,
    deleted bool NOT NULL DEFAULT FALSE,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
ALTER SEQUENCE virtual_table_id_seq OWNED BY virtual_table.id;

CREATE SEQUENCE IF NOT EXISTS virtual_field_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.virtual_field (
    id BIGINT PRIMARY KEY DEFAULT nextval('virtual_field_id_seq'),
    table_id int8 NOT NULL,
    "name" varchar(255) NOT NULL,
    "path" varchar(255) NOT NULL,
    type varchar(255) NOT NULL,
    comment varchar(255) NULL,
    nullable bool NULL,
    primary_key bool NULL,
    default_value varchar(255) NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
ALTER SEQUENCE virtual_field_id_seq OWNED BY virtual_field.id;

CREATE SEQUENCE IF NOT EXISTS job_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.job (
    id BIGINT PRIMARY KEY DEFAULT nextval('job_id_seq'),
    project_id int8 NOT NULL,
    "name" varchar(255) NOT NULL,
    description varchar(255) NULL,
    job_type int2 NOT NULL,
    schedule_type int2 NOT NULL,
    cron varchar(255) NULL,
    start_time timestamp with time zone NULL,
    end_time timestamp with time zone NULL,
    config JSONB NOT NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
ALTER SEQUENCE job_id_seq OWNED BY job.id;
COMMENT ON COLUMN public.job.job_type IS '1:batch 2:stream';
COMMENT ON COLUMN public.job.schedule_type IS '1:run_now 2:schedule 3:not_run';

CREATE SEQUENCE IF NOT EXISTS job_instance_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.job_instance (
    id BIGINT PRIMARY KEY DEFAULT nextval('job_instance_id_seq'),
    job_id int8 NOT NULL,
    project_id int8 NOT NULL,
    status int2 NOT NULL,
    seatunnel_id varchar(255) NULL,
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
ALTER SEQUENCE job_instance_id_seq OWNED BY job_instance.id;
COMMENT ON COLUMN public.job_instance.status IS '0:waiting, 1:submitted, 2:running, 3:success, 4:failure, 5:canceled';

CREATE SEQUENCE IF NOT EXISTS connector_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.connector (
    id BIGINT PRIMARY KEY DEFAULT nextval('connector_id_seq'),
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,    
    plugin_name VARCHAR(50) NOT NULL,
    support_source BOOLEAN NOT NULL DEFAULT FALSE,
    support_sink BOOLEAN NOT NULL DEFAULT FALSE,
    is_stream BOOLEAN NOT NULL DEFAULT FALSE,
    virtual_table BOOLEAN NOT NULL DEFAULT FALSE,
    support_upsert BOOLEAN NOT NULL DEFAULT TRUE,
    properties JSONB NOT NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
ALTER SEQUENCE connector_id_seq OWNED BY connector.id;

CREATE SEQUENCE IF NOT EXISTS connector_version_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.connector_version (
    id BIGINT PRIMARY KEY DEFAULT nextval('connector_version_id_seq'),
    connector varchar(50) NOT NULL,
    label VARCHAR(50) NOT NULL,
    value VARCHAR(50) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(255) NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL,
    UNIQUE(connector, label)
);
ALTER SEQUENCE connector_version_id_seq OWNED BY connector_version.id;

-- Insert database connectors
INSERT INTO public.connector (name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) VALUES
('MySQL', 'Database', 'JDBC-MYSQL', true, true, false, false, true, '{"driver":"com.mysql.cj.jdbc.Driver","host":"localhost","port":3306,"url":"jdbc:mysql://localhost:3306/default?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"}', 1000, 1000, now(), now()),
('PostgreSQL', 'Database', 'JDBC-POSTGRES', true, true, false, false, true, '{"driver":"org.postgresql.Driver","host":"localhost","port":5432,"url":"jdbc:postgresql://localhost:5432/"}', 1000, 1000, now(), now()),
('Oracle', 'Database', 'JDBC-ORACLE', true, true, false, false, true, '{"driver":"oracle.jdbc.OracleDriver","host":"localhost","port":1521,"url":"jdbc:oracle:thin:@localhost:1521:ORCL"}', 1000, 1000, now(), now()),
('SQLServer', 'Database', 'JDBC-SQLSERVER', true, true, false, false, true, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","host":"localhost","port":1433,"url":"jdbc:sqlserver://localhost:1433;DatabaseName=seatunnel"}', 1000, 1000, now(), now()),
('Tidb', 'Database', 'JDBC-TIDB', true, true, false, false, true, '{"driver":"com.mysql.jdbc.Driver","host":"localhost","port":4000,"url":"jdbc:mysql://localhost:4000/seatunnel"}', 1000, 1000, now(), now()),
('Hive', 'Database', 'Hive', true, true, false, false, false, '{}', 10000, 10000, now(), now())
ON CONFLICT (name) DO NOTHING;

-- Insert CDC connectors
INSERT INTO public.connector (name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) VALUES
('MySQL-CDC', 'Database', 'MYSQL-CDC', true, false, true, false, false, '{"driver":"com.mysql.jdbc.Driver","url":"jdbc:mysql://mysql:3306"}', 10000, 10000, now(), now()),
('SQLServer-CDC', 'Database', 'SQLServer-CDC', true, false, true, false, false, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","url":"jdbc:sqlserver://sqlserver:1433"}', 10000, 10000, now(), now())
ON CONFLICT (name) DO NOTHING;

-- Insert nonstructural connectors
INSERT INTO public.connector (name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) VALUES
('Kafka', 'MQ', 'KAFKA', true, true, true, true, false, '{"bootstrap.servers":"localhost:9092","topics":"topic1"}', 10000, 10000, now(), now()),
('FTP', 'File', 'FTP', true, true, false, false, false, '{}', 10000, 10000, now(), now()),
('S3', 'File', 'S3', true, true, false, false, false, '{}', 10000, 10000, now(), now()),
('LocalFile', 'File', 'Local', true, true, false, false, false, '{}', 10000, 10000, now(), now()),
('SFTP', 'File', 'SFTP', true, true, false, false, false, '{}', 10000, 10000, now(), now())
ON CONFLICT (name) DO NOTHING;

-- Insert connector versions
INSERT INTO public.connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('MySQL', 'MySQL 5.x', 'mysql_5.6', 'false', 'MySQL 5.6 or earlier version', 10000, 10000, now(), now()),
('MySQL', 'MySQL 8.x', 'mysql_8.0', 'true', 'MySQL 5.7 or later version', 10000, 10000, now(), now()),
('SQLServer', 'SQLServer 2008', 'sqlserver_2008', 'false', 'SQLServer 2008 R2 or earlier version', 10000, 10000, now(), now()),
('SQLServer', 'SQLServer 2012+', 'sqlserver_2012', 'true', 'SQLServer 2012 or later version', 10000, 10000, now(), now()),
('MySQL-CDC', 'MySQL 8.x', 'mysql_8.0', 'true', 'MySQL 5.7 or later version', 10000, 10000, now(), now()),
('SQLServer-CDC', 'SQLServer 2012+', 'sqlserver_2012', 'true', 'SQLServer 2012 or later version', 10000, 10000, now(), now())
ON CONFLICT (connector, label) DO NOTHING;
