/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * @author: yuehan124@gmail.com
 * @date: 2026-06-05
 */
import { MinusCircleOutlined, PlusCircleOutlined, UploadOutlined } from "@ant-design/icons";
import {
  Button,
  Col, Form,
  Input, Radio, Row, Select, TreeSelect, Typography,
  Upload,
  message
} from "antd";
import { Rule } from "antd/es/form";
import FormItem from "antd/es/form/FormItem";
import { FC, memo, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { formatConnector, getConnector, saveOrUpdateConnection, testConnection } from "../../../api/connection";
import useRequest from "../../../hooks/useRequest";
import { connectionStore } from "../../../store";
import { useTranslation } from 'react-i18next';
import FtpConnectionForm from "./ftp";
import S3ConnectionForm from "./s3";
import "./index.less";
const { Option } = Select;


interface IProps {
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const values = connectionStore.getValues();
  const config = values.config || {};
  const connection = { ...values, ...config, "extraParams": values.extraParams || [{}] };

  const [form] = Form.useForm();
  const [selectedConnector, setSelectedConnector] = useState<string>(connection.connector);
  const navigate = useNavigate();

  const reqConnector = useRequest(getConnector, {
    wrapperFun: formatConnector
  });

  useEffect(() => {
    reqConnector.caller();
  }, []);

  const onTypeChange = (newValue: string) => {
    setSelectedConnector(newValue);
    let name = form.getFieldsValue().name;
    form.resetFields();
    form.setFieldsValue({ name: name, connector: newValue });
  };

  const form2Connection = () => {
    const values = form.getFieldsValue();
    const connector = values.connector;
    const id = values.id;
    const name = values.name;
    const description = values.description;
    let host = values.host;
    let port = Number.parseInt(values.port);
    const database = values.database;
    const svcType = values.svcType;
    const user = values.user;
    const passwd = values.passwd;
    const url = values.url;
    // kafka property
    const broker = values.broker;
    // hive property
    const metastore_uri = values.metastore_uri;
    if (metastore_uri) {
      // retrieve metastore's host and port: thrift://hive-metastore:9083
      host = metastore_uri.split('://')[1].split(':')[0];
      port = metastore_uri.split('://')[1].split(':')[1];
    }
    const hdfsSite = values.hdfsSite;
    const hiveSite = values.hiveSite;
    const kerberosPrincipal = values.kerberosPrincipal;
    const kerberosKeytab = values.kerberosKeytab;
    const kerberosKrb5Conf = values.kerberosKrb5Conf;
    const config = { database, svcType, url, broker, metastore_uri, hdfsSite, hiveSite, kerberosPrincipal, kerberosKeytab, kerberosKrb5Conf };
    const type = connectionStore.getValues().type;
    const conn = { id, connector, name, description, host, port, user, passwd, config, type };
    return conn;
  }

  const reqSaveOrUpdate = useRequest(saveOrUpdateConnection, {
    wrapperFun: (res: any) => {
      if (res === true) {
        message.success(t('connection.create.message.saveSuccess'));
        navigate("/connection");
      } else {
        message.error(t('connection.create.message.saveFailed'));
      }
    }
  });

  const onFinish = (values: any) => {
    let conn = form2Connection();
    reqSaveOrUpdate.caller(conn);
  };

  const reqTestConn = useRequest(testConnection);

  const onTestConn = () => {
    let conn = form2Connection();
    reqTestConn.caller(conn).then((result) => {
      // result is now { code, data, msg } structure
      console.log('Test connection result:', result);
    });
  }

  const onCancel = () => {
    navigate("/connection");
  };

  const generateUrl = (e: any) => {
    const values = form.getFieldsValue();
    const host = values.host || "127.0.0.1";
    const port = values.port || 3306;
    const database = values.database;
    const svcType = values.svcType;
    let jdbcUrl = "";
    switch (values.connector) {
      case "MySQL":
        jdbcUrl = `jdbc:mysql://${host}:${port}/${database}?characterEncoding=utf-8&useSSL=false`;
        break;
      case "PostgreSQL":
        jdbcUrl = `jdbc:postgresql://${host}:${port}/${database}`;
        break;
      case "Oracle":
        if (svcType === "SID") {
          jdbcUrl = `jdbc:oracle:thin:@${host}:${port}:${database}`;
        } else {
          jdbcUrl = `jdbc:oracle:thin:@//${host}:${port}/${database}`;
        }
        break;
      case "SQLServer":
        jdbcUrl = `jdbc:sqlserver://${host}:${port};databaseName=${database};trustServerCertificate=true`;
        break;
      default:
        jdbcUrl = `jdbc:mysql://${host}:${port}/${database}`;
        break;
    }
    // set form item url's value
    form.setFieldsValue({ url: jdbcUrl });
  };

  const validateConnectionName = async (rule: Rule, value: string) => {
    return Promise.resolve();
  };

  const renderDaynamic = () => {
    if (selectedConnector === 'MySQLCDC' || selectedConnector === 'SQLServerCDC') {
      return (
        <>
          <Form.Item name="url" label={t('connection.create.form.jdbcUrl')} rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="user" label={t('connection.create.form.username')} rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="pass" label={t('connection.create.form.password')} rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="timezone" label={t('connection.create.form.timezone')}>
            <Input placeholder="UTC+8" />
          </Form.Item>
        </>
      );
    } else if (selectedConnector === 'Kafka') {
      return (
        <>
          <Form.Item name="broker" label={t('connection.create.form.broker')} rules={[{ required: true }]}>
            <Input placeholder={t('connection.create.placeholder.broker')} />
          </Form.Item>
          <Form.Item name="config" label={t('connection.create.form.kafkaConfig')}>
            <Input.TextArea placeholder={t('connection.create.placeholder.kafkaConfig')} />
          </Form.Item>
        </>
      );
    } else if (selectedConnector == 'FTP' || selectedConnector == 'SFTP') {
      return <FtpConnectionForm />
    } else if (selectedConnector === 'S3') {
      return <S3ConnectionForm />
    } else if (selectedConnector === 'LocalFile') {
      return <></>
    } else if (selectedConnector === 'Hive') {
      return (
        <>
          <Form.Item name="metastore_uri" label={t('connection.create.form.metastoreUri')} rules={[{ required: true }]}>
            <Input placeholder="thrift://127.0.0.1:9083" />
          </Form.Item>
          <Form.Item name="hdfsSite" label={t('connection.create.form.hdfsSite')}>
            <Upload >
              <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
            </Upload>
          </Form.Item>
          <Form.Item name="hiveSite" label={t('connection.create.form.hiveSite')}>
            <Upload >
              <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
            </Upload>
          </Form.Item>
          <Form.Item name="kerberosPrincipal" label={t('connection.create.form.kerberosPrincipal')}>
            <Input placeholder="kerberos_principal" />
          </Form.Item>
          <Form.Item name="kerberosKeytab" label={t('connection.create.form.kerberosKeytab')}>
            <Upload >
              <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
            </Upload>
          </Form.Item>
          <Form.Item name="kerberosKrb5Conf" label={t('connection.create.form.kerberosKrb5Conf')}>
            <Upload >
              <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
            </Upload>
          </Form.Item>
        </>
      );
    } else {
      return (
        <>
          <Form.Item name="hostAndPort" label={t('connection.create.form.host')} style={{ marginBottom: 0 }}>
            <Form.Item name="host" rules={[{ required: true }]} style={{ display: 'inline-block', width: 'calc(50% - 8px)' }}>
              <Input placeholder={t('connection.create.placeholder.host')} onChange={generateUrl} />
            </Form.Item>
            <Form.Item name="port" label={t('connection.create.form.port')} rules={[{ required: true }]} style={{ display: 'inline-block', width: 'calc(50%)', margin: '0 0 0 8px' }}>
              <Input placeholder={t('connection.create.placeholder.port')} onChange={generateUrl} />
            </Form.Item>
          </Form.Item>
          {selectedConnector === 'Oracle' ? (
            <>
              <Form.Item name="database" label={t('connection.create.form.database')} rules={[{ required: true }]}>
                <Input placeholder="" onChange={generateUrl} />
              </Form.Item>
              <FormItem name="svcType" label={t('connection.create.form.svcType')} rules={[{ required: true }]}>
                <Radio.Group onChange={generateUrl}>
                  <Radio value="svcName">{t('connection.create.form.svcName')}</Radio>
                  <Radio value="SID">{t('connection.create.form.sid')}</Radio>
                </Radio.Group>
              </FormItem>
            </>
          ) : (
            <Form.Item name="database" label={t('connection.create.form.database')}>
              <Input placeholder={t('connection.create.placeholder.database')} onChange={generateUrl} />
            </Form.Item>
          )}
          <Form.Item name="url" label={t('connection.create.form.jdbcUrl')} rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="user" label={t('connection.create.form.username')} rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="passwd" label={t('connection.create.form.password')} rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
        </>
      )
    }
  };

  return (
    <>
      <Form
        form={form}
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 10 }}
        onFinish={onFinish}
        initialValues={connection}
        autoComplete="off"
      >
        <Form.Item name="id" style={{display: 'none'}}>
          <Input readOnly />
        </Form.Item>
        <Form.Item name="name" label={t('connection.create.form.name')} rules={[{ required: true }, { validator: validateConnectionName }]}>
          <Input placeholder={t('connection.create.placeholder.name')} />
        </Form.Item>
        <Form.Item name="connector" label={t('connection.create.form.type')} rules={[{ required: true }]}>
          <TreeSelect
            showSearch
            style={{ width: '100%', textAlign: 'left' }}
            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
            placeholder={t('connection.create.placeholder.selectType')}
            allowClear
            treeDefaultExpandAll
            onChange={onTypeChange}
            treeData={reqConnector.data}
          />
        </Form.Item>
        <Form.Item name="description" label={t('connection.create.form.description')}>
          <Input.TextArea placeholder={t('connection.create.placeholder.description')} rows={1} />
        </Form.Item>
        {renderDaynamic()}
        <Form.List name="extraParams">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => (<>
                <Form.Item label={t('connection.create.form.extraParams')}>
                  <Form.Item
                    {...restField}
                    name={[name, 'first']}
                    style={{ display: 'inline-block', width: 'calc(50% - 80px)', margin: '0 8px 0 0' }}
                  >
                    <Input placeholder={t('connection.create.placeholder.paramName')} />
                  </Form.Item>
                  <Typography.Link href="#API" style={{ display: 'inline-block' }}> = </Typography.Link>
                  <Form.Item
                    {...restField}
                    name={[name, 'last']}
                    style={{ display: 'inline-block', width: 'calc(50% - 80px)', margin: '0 0 0 8px' }}
                  >
                    <Input placeholder={t('connection.create.placeholder.paramValue')} />
                  </Form.Item>
                  {index !== 0 && <MinusCircleOutlined onClick={() => remove(name)} style={{ margin: '10px 2px 10px 2px' }} />}
                  <PlusCircleOutlined onClick={() => add()} style={{ margin: '10px 2px 10px 2px' }} />
                </Form.Item>
              </>))}
            </>
          )}
        </Form.List>
        <Row>
          <Col span={20} style={{ marginLeft: 50, marginTop: 20, textAlign: 'left' }}>
            <Button htmlType="button" onClick={onTestConn} style={{ marginRight: 20 }}>{t('connection.create.button.test')}</Button>
            <Button htmlType="button" onClick={onCancel} style={{ marginRight: 20 }}>{t('connection.create.button.cancel')}</Button>
            <Button type="primary" htmlType="submit">{t('connection.create.button.save')}</Button>
          </Col>
        </Row>
      </Form>
    </>
  );
};

const ConnectionForm = memo(S);

export default ConnectionForm;