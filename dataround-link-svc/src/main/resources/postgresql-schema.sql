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
    driver VARCHAR(50) NULL,
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
('MySQL', 'Database', 'JDBC-MYSQL', true, true, false, false, true, '{"driver":"com.mysql.cj.jdbc.Driver","host":"localhost","port":3306,"url":"jdbc:mysql://localhost:3306/default?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"}', 10000, 10000, now(), now()),
('PostgreSQL', 'Database', 'JDBC-POSTGRES', true, true, false, false, true, '{"driver":"org.postgresql.Driver","host":"localhost","port":5432,"url":"jdbc:postgresql://localhost:5432/"}', 10000, 10000, now(), now()),
('Oracle', 'Database', 'JDBC-ORACLE', true, true, false, false, true, '{"driver":"oracle.jdbc.OracleDriver","host":"localhost","port":1521,"url":"jdbc:oracle:thin:@localhost:1521:ORCL"}', 10000, 10000, now(), now()),
('SQLServer', 'Database', 'JDBC-SQLSERVER', true, true, false, false, true, '{"driver":"com.microsoft.sqlserver.jdbc.SQLServerDriver","host":"localhost","port":1433,"url":"jdbc:sqlserver://localhost:1433;DatabaseName=seatunnel"}', 10000, 10000, now(), now()),
('Tidb', 'Database', 'JDBC-TIDB', true, true, false, false, true, '{"driver":"com.mysql.jdbc.Driver","host":"localhost","port":4000,"url":"jdbc:mysql://localhost:4000/seatunnel"}', 10000, 10000, now(), now()),
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
INSERT INTO public.connector_version (connector, label, value, driver, is_default, description, create_by, update_by, create_time, update_time) VALUES
('MySQL', 'MySQL 5.x', 'mysql_5.6', 'com.mysql.jdbc.Driver', 'false', 'MySQL 5.6 or earlier version', 10000, 10000, now(), now()),
('MySQL', 'MySQL 8.x', 'mysql_8.0', 'com.mysql.cj.jdbc.Driver', 'true', 'MySQL 5.7 or later version', 10000, 10000, now(), now()),
('MySQL-CDC', 'MySQL 8.x', 'mysql_8.0', 'com.mysql.cj.jdbc.Driver', 'true', 'MySQL 5.7 or later version', 10000, 10000, now(), now()),
('SQLServer-CDC', 'SQLServer', 'sqlserver', 'com.microsoft.sqlserver.jdbc.SQLServerDriver', 'true', 'SQLServer 2008 or later version', 10000, 10000, now(), now())
ON CONFLICT (connector, label) DO NOTHING;


-- Insert users
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.user (
	id BIGINT PRIMARY KEY DEFAULT nextval('user_id_seq'),
	"name" varchar(255) NOT NULL,
	cellphone varchar(50) NOT NULL,
	email varchar(255) NOT NULL,
	passwd varchar(255) NOT NULL,
	avatar varchar(500),
	gender char(1),
	birthday date,
	department varchar(255),
	position varchar(255),
	address varchar(500),
	wechat varchar(100),
	status int4 NOT NULL DEFAULT 1,
	remark varchar(1000),
	last_login_ip varchar(50),
	last_login_time timestamp with time zone,
    creator_id int8 NOT NULL,
	updater_id int8 NOT NULL,
	create_time timestamp with time zone NOT NULL,
	update_time timestamp with time zone NOT NULL,
	UNIQUE(name)
);
COMMENT ON COLUMN public.user.name IS 'login field';
COMMENT ON COLUMN public.user.cellphone IS 'login field';
COMMENT ON COLUMN public.user.email IS 'login field';
INSERT INTO public.user("name", cellphone, email, passwd, status, creator_id, updater_id, create_time, update_time) 
VALUES ('admin', '13800000000', 'admin@dataround.io', '38ae9456d40553d1213ea238c2f7a49f52762d19fc29939968d6e0a51a569dcb', 1, 10000, 10000, now(), now()) 
ON CONFLICT (name) DO NOTHING;

CREATE SEQUENCE IF NOT EXISTS project_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.project (
	id BIGINT PRIMARY KEY DEFAULT nextval('project_id_seq'),
	"name" varchar(255) NOT NULL,
	description varchar(1000) NOT NULL,
	creator_id int8 NOT NULL,
	create_time timestamp with time zone NOT NULL,
	UNIQUE(name)
);
INSERT INTO public.project("name", description, creator_id, create_time) 
VALUES ('default', '', 10000, now()) 
ON CONFLICT (name) DO NOTHING;

CREATE SEQUENCE IF NOT EXISTS project_user_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.project_user (
	id BIGINT PRIMARY KEY DEFAULT nextval('project_user_id_seq'),
	user_id int8 NOT NULL,
	project_id int8 NOT NULL,
	is_admin bool NOT NULL default false,
	selected bool NOT NULL default false,
	create_time timestamp with time zone,
	UNIQUE(user_id, project_id)
);
INSERT INTO public.project_user(user_id, project_id, is_admin, selected, create_time) 
VALUES (10000, 10000, true, true, now()) 
ON CONFLICT (user_id, project_id) DO NOTHING;

CREATE SEQUENCE IF NOT EXISTS user_role_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.user_role (
	id BIGINT PRIMARY KEY DEFAULT nextval('user_role_id_seq'),
	user_id int8 NOT NULL,
	role_id int8 NOT NULL,
	create_time timestamp with time zone NOT NULL,
	UNIQUE(user_id, role_id)
);
-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_user_role_user ON public.user_role(user_id);

CREATE SEQUENCE IF NOT EXISTS role_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.role (
	id BIGINT PRIMARY KEY DEFAULT nextval('role_id_seq'),
	"name" varchar(255) NOT NULL,
	description varchar(500) NOT NULL,
	create_time timestamp with time zone NOT NULL,
	UNIQUE(name)
);

CREATE SEQUENCE IF NOT EXISTS role_resource_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.role_resource (
	id BIGINT PRIMARY KEY DEFAULT nextval('role_resource_id_seq'),
	role_id int8 NOT NULL,
	resource_id int8 NOT NULL,
	create_time timestamp with time zone NOT NULL,
	UNIQUE(role_id, resource_id)
);
-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_role_resource_role ON public.role_resource(role_id);

CREATE SEQUENCE IF NOT EXISTS resource_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.resource (
	id BIGINT PRIMARY KEY DEFAULT nextval('resource_id_seq'),
	pid int8 NOT NULL,
	i18n_name varchar(255) NOT NULL,
	res_key varchar(255) NOT NULL,
	description varchar(500),
	create_time timestamp with time zone NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS resource_api_id_seq START WITH 10000;
CREATE TABLE IF NOT EXISTS public.resource_api (
	id BIGINT PRIMARY KEY DEFAULT nextval('resource_api_id_seq'),
	resource_id int8 NOT NULL,
	method varchar(10) NULL,
	path varchar(255) NOT NULL,
	UNIQUE(resource_id, method, path)
);

-- Insert default admin role
INSERT INTO public.role(name, description, create_time) 
VALUES ('Admin', 'System Administrator', now()) 
ON CONFLICT (name) DO NOTHING;

-- Assign admin role to admin user
INSERT INTO public.user_role(user_id, role_id, create_time) 
VALUES (10000, 10000, now()) 
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Insert default resources for admin role
-- UI Resources (menus and buttons)
INSERT INTO public.resource(id, pid, i18n_name, res_key, description, create_time) VALUES
(10000, 0, 'resource.menu.permission', '', '', now()),
(10002, 10000, 'resource.menu.project', 'menu:project', '', now()),
(10003, 10000, 'resource.menu.projectMember', 'menu:projectMember', '', now()),
(10004, 10000, 'resource.menu.user', 'menu:user', '', now()),
(10005, 10000, 'resource.menu.permission', 'menu:permission', '', now()),
(10006, 10000, 'resource.menu.myAccount', 'menu:myAccount', '', now()),

(10007, 10004, 'resource.btn.user.add', 'btn:user:add', '', now()),
(10008, 10004, 'resource.btn.user.edit', 'btn:user:edit', '', now()),
(10009, 10004, 'resource.btn.user.delete', 'btn:user:delete', '', now()),
(10010, 10004, 'resource.btn.user.status', 'btn:user:status', '', now()),

(10011, 10005, 'resource.menu.role', 'menu:role', '', now()),
(10012, 10005, 'resource.menu.userRole', 'menu:userRole', '', now()),
(10013, 10005, 'resource.menu.resource', 'menu:resource', '', now())
ON CONFLICT (id) DO NOTHING;

-- Insert API resources for each menu
INSERT INTO public.resource_api(id, resource_id, method, path) VALUES
-- Project Management APIs
(10000, 10002, 'GET', '/api/project/all'),
(10001, 10002, 'GET', '/api/project/my'),
(10002, 10002, 'POST', '/api/project/saveOrUpdate'),
(10003, 10002, 'DELETE', '/api/project/{id}'),
(10004, 10002, 'GET', '/api/project/member/list'),
(10005, 10002, 'POST', '/api/project/member/save'),
(10006, 10002, 'DELETE', '/api/project/member/{id}'),

-- Project Member APIs
(10007, 10003, 'GET', '/api/project/member/list'),
(10008, 10003, 'POST', '/api/project/member/save'),
(10009, 10003, 'DELETE', '/api/project/member/{id}'),

-- User Management APIs
(10010, 10004, 'GET', '/api/user/list'),
(10011, 10004, 'GET', '/api/user/info'),
(10012, 10004, 'POST', '/api/user/saveOrUpdate'),
(10013, 10004, 'POST', '/api/user/updatePasswd'),

-- System Permission APIs
(10014, 10005, 'GET', '/api/role/list'),
(10015, 10005, 'GET', '/api/role/all'),
(10016, 10005, 'POST', '/api/role/saveOrUpdate'),
(10017, 10005, 'DELETE', '/api/role/{id}'),
(10018, 10005, 'GET', '/api/role/{id}/resources'),
(10019, 10005, 'POST', '/api/role/{id}/resources'),
(10020, 10005, 'GET', '/api/resource/all'),
(10021, 10005, 'GET', '/api/resource/user'),
(10022, 10005, 'POST', '/api/resource/saveOrUpdate'),
(10023, 10005, 'DELETE', '/api/resource/{id}'),
(10024, 10005, 'GET', '/api/userRole/list'),
(10025, 10005, 'POST', '/api/userRole/assign'),

-- My Account APIs
(10026, 10006, 'GET', '/api/user/info'),
(10027, 10006, 'POST', '/api/user/updatePasswd'),

-- Role Management APIs
(10028, 10011, 'GET', '/api/role/list'),
(10029, 10011, 'GET', '/api/role/all'),
(10030, 10011, 'POST', '/api/role/saveOrUpdate'),
(10031, 10011, 'DELETE', '/api/role/{id}'),
(10032, 10011, 'GET', '/api/role/{id}/resources'),
(10033, 10011, 'POST', '/api/role/{id}/resources'),

-- User Role APIs
(10034, 10012, 'GET', '/api/userRole/list'),
(10035, 10012, 'POST', '/api/userRole/assign'),

-- Resource Management APIs
(10036, 10013, 'GET', '/api/resource/all'),
(10037, 10013, 'GET', '/api/resource/user'),
(10038, 10013, 'POST', '/api/resource/saveOrUpdate'),
(10039, 10013, 'DELETE', '/api/resource/{id}')
ON CONFLICT (id) DO NOTHING;

-- Assign all resources to admin role
INSERT INTO public.role_resource(role_id, resource_id, create_time)
SELECT 10000, id, now() FROM public.resource
ON CONFLICT (role_id, resource_id) DO NOTHING;
