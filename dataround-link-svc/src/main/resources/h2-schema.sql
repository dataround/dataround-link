-- H2 schema for dataround_link --

CREATE SEQUENCE IF NOT EXISTS connection_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS connection (
    id BIGINT DEFAULT NEXT VALUE FOR connection_id_seq,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    connector VARCHAR(50) NOT NULL,
    connector_version_id BIGINT NULL,
    host VARCHAR(255) NULL,
    port INT NULL,
    "user" VARCHAR(255) NULL,
    passwd VARCHAR(255) NULL,
    config JSON NOT NULL,
    "description" VARCHAR(255) NULL,
    create_by BIGINT NOT NULL,
    update_by BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_connection PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS virtual_table_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS virtual_table (
    id BIGINT DEFAULT NEXT VALUE FOR virtual_table_id_seq,
    connection_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    database VARCHAR(255) NOT NULL,
    table_name VARCHAR(255) NOT NULL,
    table_config VARCHAR(1024) NOT NULL,
    "description" VARCHAR(255) NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    create_by BIGINT NOT NULL,
    update_by BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_virtual_table PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS virtual_field_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS virtual_field (
    id BIGINT DEFAULT NEXT VALUE FOR virtual_field_id_seq,
    table_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    "comment" VARCHAR(255) NULL,
    nullable BOOLEAN NULL,
    primary_key BOOLEAN NULL,
    default_value VARCHAR(255) NULL,
    create_by BIGINT NOT NULL,
    update_by BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_virtual_field PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS job_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS job (
    id BIGINT DEFAULT NEXT VALUE FOR job_id_seq,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    "description" VARCHAR(255) NULL,
    job_type SMALLINT NOT NULL,
    schedule_type SMALLINT NOT NULL,
    cron VARCHAR(255) NULL,
    start_time TIMESTAMP WITH TIME ZONE NULL,
    end_time TIMESTAMP WITH TIME ZONE NULL,
    config JSON NOT NULL,
    create_by BIGINT NOT NULL,
    update_by BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_job PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS job_instance_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS job_instance (
    id BIGINT DEFAULT NEXT VALUE FOR job_instance_id_seq,
    job_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    status SMALLINT NOT NULL,
    seatunnel_id VARCHAR(255) NULL,
    start_time TIMESTAMP WITH TIME ZONE NULL,
    end_time TIMESTAMP WITH TIME ZONE NULL,
    read_count BIGINT NULL,
    write_count BIGINT NULL,
    read_qps DOUBLE PRECISION NULL,
    write_qps DOUBLE PRECISION NULL,
    read_bytes BIGINT NULL,
    write_bytes BIGINT NULL,
    log_content CLOB NULL,
    update_by BIGINT NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_job_instance PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS connector_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS connector (
    id BIGINT DEFAULT NEXT VALUE FOR connector_id_seq,
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    plugin_name VARCHAR(50) NOT NULL,
    support_source BOOLEAN NOT NULL DEFAULT FALSE,
    support_sink BOOLEAN NOT NULL DEFAULT FALSE,
    is_stream BOOLEAN NOT NULL DEFAULT FALSE,
    virtual_table BOOLEAN NOT NULL DEFAULT FALSE,
    support_upsert BOOLEAN NOT NULL DEFAULT TRUE,
    properties JSON NOT NULL,
    create_by BIGINT NOT NULL,
    update_by BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_connector PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS connector_version_id_seq START WITH 10000;

CREATE TABLE IF NOT EXISTS connector_version (
    id BIGINT DEFAULT NEXT VALUE FOR connector_version_id_seq,
    connector VARCHAR(50) NOT NULL,
    label VARCHAR(50) NOT NULL,
    value VARCHAR(50) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(255) NULL,
    create_by BIGINT NOT NULL,
    update_by BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_connector_version PRIMARY KEY (id),
    CONSTRAINT uk_connector_version UNIQUE(connector, label)
);

-- Insert database connectors
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10000, 'MySQL', 'Database', 'JDBC-MYSQL', TRUE, TRUE, FALSE, FALSE, TRUE, '{"driver":"com.mysql.cj.jdbc.Driver","host":"localhost","port":3306,"url":"jdbc:mysql://localhost:3306/default?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"}', 1000, 1000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10001, 'PostgreSQL', 'Database', 'JDBC-POSTGRES', TRUE, TRUE, FALSE, FALSE, TRUE, '{"driver":"org.postgresql.Driver","host":"localhost","port":5432,"url":"jdbc:postgresql://localhost:5432/"}', 1000, 1000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10002, 'Oracle', 'Database', 'JDBC-ORACLE', TRUE, TRUE, FALSE, FALSE, TRUE, '{"driver":"oracle.jdbc.OracleDriver","host":"localhost","port":1521,"url":"jdbc:oracle:thin:@localhost:1521:ORCL"}', 1000, 1000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10003, 'SQLServer', 'Database', 'JDBC-SQLSERVER', TRUE, TRUE, FALSE, FALSE, TRUE, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","host":"localhost","port":1433,"url":"jdbc:sqlserver://localhost:1433;DatabaseName=seatunnel"}', 1000, 1000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10004, 'Tidb', 'Database', 'JDBC-TIDB', TRUE, TRUE, FALSE, FALSE, TRUE, '{"driver":"com.mysql.jdbc.Driver","host":"localhost","port":4000,"url":"jdbc:mysql://localhost:4000/seatunnel"}', 1000, 1000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10005, 'Hive', 'Database', 'Hive', TRUE, TRUE, FALSE, FALSE, TRUE, '{}', 10000, 10000, NOW(), NOW());

-- Insert CDC connectors
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10006, 'MySQL-CDC', 'Database', 'MYSQL-CDC', TRUE, FALSE, TRUE, FALSE, FALSE, '{"driver":"com.mysql.jdbc.Driver","url":"jdbc:mysql://mysql:3306"}', 10000, 10000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10007, 'SQLServer-CDC', 'Database', 'SQLServer-CDC', TRUE, FALSE, TRUE, FALSE, FALSE, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","url":"jdbc:sqlserver://sqlserver:1433"}', 10000, 10000, NOW(), NOW());

-- Insert nonstructural connectors
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10008, 'Kafka', 'MQ', 'KAFKA', TRUE, TRUE, TRUE, TRUE, FALSE, '{"bootstrap.servers":"localhost:9092","topics":"topic1"}', 10000, 10000, NOW(), NOW());
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10009, 'FTP', 'File', 'FTP', TRUE, TRUE, FALSE, FALSE, FALSE, '{}', 10000, 10000, '2025-07-18 17:28:17', '2025-07-18 17:28:20');
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10010, 'S3', 'File', 'S3', TRUE, TRUE, FALSE, FALSE, FALSE, '{}', 10000, 10000, '2025-07-20 21:14:22', '2025-07-20 21:14:25');
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10011, 'LocalFile', 'File', 'Local', TRUE, TRUE, FALSE, FALSE, FALSE, '{}', 10000, 10000, '2025-07-22 10:59:37', '2025-07-22 10:59:40');
MERGE INTO connector (id, name, type, plugin_name, support_source, support_sink, is_stream, virtual_table, support_upsert, properties, create_by, update_by, create_time, update_time) 
VALUES (10012, 'SFTP', 'File', 'SFTP', TRUE, TRUE, FALSE, FALSE, FALSE, '{}', 10000, 10000, '2025-07-27 08:40:38', '2025-07-27 08:40:41');

-- Insert connector versions
MERGE INTO connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('MySQL', 'MySQL 5.x', 'mysql5.6', 'false', 'MySQL 5.6 or earlier version', 10000, 10000, now(), now());
MERGE INTO connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('MySQL', 'MySQL 8.x', 'mysql8.0', 'true', 'MySQL 5.7 or later version', 10000, 10000, now(), now());
MERGE INTO connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('SQLServer', 'SQLServer 2008', 'sqlserver2008', 'false', 'SQLServer 2008 R2 or earlier version', 10000, 10000, now(), now());
MERGE INTO connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('SQLServer', 'SQLServer 2012+', 'sqlserver2012', 'true', 'SQLServer 2012 or later version', 10000, 10000, now(), now());
MERGE INTO connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('MySQL-CDC', 'MySQL 8.x', 'mysql8.0', 'true', 'MySQL 5.7 or later version', 10000, 10000, now(), now());
MERGE INTO connector_version (connector, label, value, is_default, description, create_by, update_by, create_time, update_time) VALUES
('SQLServer-CDC', 'SQLServer 2012+', 'sqlserver2012', 'true', 'SQLServer 2012 or later version', 10000, 10000, now(), now());
