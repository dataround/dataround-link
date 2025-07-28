-- dataround link database init script
CREATE TABLE public.connection (
    id int8 NOT NULL PRIMARY KEY,
    project_id int8 NOT NULL,
    "name" varchar NOT NULL,
    type varchar NOT NULL,
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
CREATE TABLE public.virtual_table (
    id int8 NOT NULL PRIMARY KEY,
    connection_id int8 NOT NULL,
    project_id int8 NOT NULL,
    "database" varchar NOT NULL,
    table_name varchar NOT NULL,
    table_config varchar NOT NULL,
    description varchar NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);
CREATE TABLE public.virtual_field (
    id int8 NOT NULL PRIMARY KEY,
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
CREATE TABLE public.job (
    id int8 NOT NULL PRIMARY KEY,
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

CREATE TABLE public.job_instance (
    id int8 NOT NULL PRIMARY KEY,
    job_id int8 NOT NULL,
    project_id int8 NOT NULL,
    status int2 NOT NULL,
    job_config varchar NULL,
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

CREATE TABLE connector (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    plugin_name VARCHAR(100) NOT NULL,
    support_source BOOLEAN NOT NULL DEFAULT FALSE,
    support_sink BOOLEAN NOT NULL DEFAULT FALSE,
    is_stream BOOLEAN NOT NULL DEFAULT FALSE,
    virtual_table BOOLEAN NOT NULL DEFAULT FALSE,
    lib_dir VARCHAR(100) NOT NULL,
    properties JSONB NOT NULL,
    create_by int8 NOT NULL,
    update_by int8 NOT NULL,
    create_time timestamp with time zone NOT NULL,
    update_time timestamp with time zone NOT NULL
);

-- Insert database connectors
INSERT INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, lib_dir, properties, create_by, update_by) VALUES
(1, 'MySQL', 'Database', 'JDBC-MYSQL', true, true, false, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-jdbc/target', '{"driver":"com.mysql.cj.jdbc.Driver","host":"localhost","port":3306,"url":"jdbc:mysql://localhost:3306/default?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"}', 1000, 1000),
(2, 'PostgreSQL', 'Database', 'JDBC-POSTGRES', true, true, false, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-jdbc/target', '{"driver":"org.postgresql.Driver","host":"localhost","port":5432,"url":"jdbc:postgresql://localhost:5432/"}', 1000, 1000),
(3, 'Oracle', 'Database', 'JDBC-ORACLE', true, true, false, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-jdbc/target', '{"driver":"oracle.jdbc.driver.OracleDriver","host":"localhost","port":1521,"url":"jdbc:oracle:thin:@localhost:1521:ORCL"}', 1000, 1000),
(4, 'SQLServer', 'Database', 'JDBC-SQLSERVER', true, true, false, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-jdbc/target', '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","host":"localhost","port":1433,"url":"jdbc:sqlserver://localhost:1433;DatabaseName=seatunnel"}', 1000, 1000),
(5, 'Tidb', 'Database', 'JDBC-TIDB', true, true, false, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-jdbc/target', '{"driver":"com.mysql.jdbc.Driver","host":"localhost","port":4000,"url":"jdbc:mysql://localhost:4000/seatunnel"}', 1000, 1000),
(6, 'Hive', 'Database', 'Hive', true, true, false, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-hive/target', '{}', 1000, 1000);

-- Insert CDC connectors
INSERT INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, lib_dir, properties, create_by, update_by) VALUES
(7, 'MySQL-CDC', 'Database', 'MYSQL-CDC', true, false, true, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-cdc/target', '{"driver":"com.mysql.jdbc.Driver","url":"jdbc:mysql://mysql:3306"}', 1000, 1000),
(8, 'SQLServer-CDC', 'Database', 'SQLServer-CDC', true, false, true, false, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-cdc/target', '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","url":"jdbc:sqlserver://sqlserver:1433"}', 1000, 1000);

-- Insert nonstructural connectors
INSERT INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, lib_dir, properties, create_by, update_by) VALUES
(9, 'Kafka', 'MQ', 'KAFKA', true, true, true, true, 'D:/github/dataround-link/dataround-link-connector/dataround-link-connector-kafka/target', '{"bootstrap.servers":"localhost:9092","topics":"topic1"}', 1000, 1000); 