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
import { MinusCircleOutlined, PlusCircleOutlined } from "@ant-design/icons";
import {
  Button,
  Col, Form,
  Input, Radio, Row, Select, TreeSelect, Typography,
  message
} from "antd";
import { Rule } from "antd/es/form";
import { FC, memo, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { formatConnector, getConnector, saveOrUpdateConnection, testConnection } from "../../../api/connection";
import useRequest from "../../../hooks/useRequest";
import { connectionStore } from "../../../store";
import { useTranslation } from 'react-i18next';
import FtpConnectionForm from "./ftp";
import S3ConnectionForm from "./s3";
import "./index.less";
import KafkaConnectionForm from "./kafka";
import CDCConnectionForm from "./cdc";
import HiveConnectionForm from "./hive";
import JdbcConnectionForm from "./jdbc";
const { Option } = Select;


interface IProps {
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const values = connectionStore.getValues();
  const configObject = values.config || {};
  const configArray = Object.entries(configObject).map(([first, last]) => ({ first, last }));
  const connection = { ...values, "configArray": configArray.length > 0 ? configArray : [{}] };

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
    if (values.configArray) {
      values.config = values.configArray.reduce((acc: any, item: any) => {
        if (item && item.first) {
          acc[item.first] = item.last;
        }
        return acc;
      }, {});
      delete values.configArray;
    }
    // hive property
    const metastore_uri = values.metastore_uri;
    if (metastore_uri) {
      // retrieve metastore's host and port: thrift://hive-metastore:9083
      values.host = metastore_uri.split('://')[1].split(':')[0];
      values.port = Number.parseInt(metastore_uri.split('://')[1].split(':')[1]);
    }    
    const conn = { ...values };
    return conn;
  }

  const reqSaveOrUpdate = useRequest(saveOrUpdateConnection, {
    wrapperFun: (res: any) => {
      if (res === true) {
        message.success(t('connection.create.message.saveSuccess'));
        // Clear connection store data after successful save
        connectionStore.setValues({});
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
    // Clear connection store data when canceling
    connectionStore.setValues({});
    navigate("/connection");
  };

  const validateConnectionName = async (rule: Rule, value: string) => {
    return Promise.resolve();
  };

  const renderDaynamic = () => {
    if (selectedConnector === 'MySQLCDC' || selectedConnector === 'SQLServerCDC') {
      return <CDCConnectionForm />;
    } else if (selectedConnector === 'Kafka') {
      return <KafkaConnectionForm />;
    } else if (selectedConnector == 'FTP' || selectedConnector == 'SFTP') {
      return <FtpConnectionForm />
    } else if (selectedConnector === 'S3') {
      return <S3ConnectionForm />
    } else if (selectedConnector === 'LocalFile') {
      return <></>
    } else if (selectedConnector === 'Hive') {
      return <HiveConnectionForm />;
    } else {
      return <JdbcConnectionForm selectedConnector={selectedConnector} form={form} />;
    }
  };

  return (
    <div className="module connection-create">
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
            dropdownStyle={{ maxHeight: 500, overflow: 'auto' }}
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
        <Form.List name="configArray">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => (<>
                <Form.Item label={t('connection.create.form.configArray')}>
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
    </div>
  );
};

const ConnectionForm = memo(S);

export default ConnectionForm;