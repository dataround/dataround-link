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
 * @date: 2026-09-26
 */
import { Form, Input, Popover, Radio } from "antd";
import FormItem from "antd/es/form/FormItem";
import { FC } from "react";
import { useTranslation } from 'react-i18next';

interface IProps {
  selectedConnector?: string;
}

const JdbcConnectionForm: FC<IProps> = ({ selectedConnector }) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();

  const generateUrl = (e: any) => {
    const values = form.getFieldsValue();
    const host = values.host || "127.0.0.1";
    const port = values.port || 3306;
    const database = values.database;
    const svcType = values.svcType;
    let jdbcUrl = "";
    let driver = "";
    switch (values.connector) {
      case "MySQL":
        driver = "com.mysql.cj.jdbc.Driver";
        jdbcUrl = `jdbc:mysql://${host}:${port}/${database}?characterEncoding=utf-8&useSSL=false`;
        break;
      case "PostgreSQL":
        driver = "org.postgresql.Driver";
        jdbcUrl = `jdbc:postgresql://${host}:${port}/${database}`;
        break;
      case "Oracle":
        driver = "oracle.jdbc.OracleDriver";
        if (svcType === "SID") {
          jdbcUrl = `jdbc:oracle:thin:@${host}:${port}:${database}`;
        } else {
          jdbcUrl = `jdbc:oracle:thin:@//${host}:${port}/${database}`;
        }
        break;
      case "SQLServer":
        driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        jdbcUrl = `jdbc:sqlserver://${host}:${port};databaseName=${database};trustServerCertificate=true`;
        break;
      default:
        driver = "com.mysql.cj.jdbc.Driver";
        jdbcUrl = `jdbc:mysql://${host}:${port}/${database}`;
        break;
    }
    // set form item url's value
    form.setFieldsValue({ url: jdbcUrl, "driver": driver });
  };

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
        <Input placeholder={t('connection.create.placeholder.jdbcUrl')} />
      </Form.Item>
      <Form.Item name="user" label={t('connection.create.form.username')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.username')} autoComplete="off" />
      </Form.Item>
      <Form.Item name="passwd" label={t('connection.create.form.password')} rules={[{ required: true }]}>
        <Input.Password placeholder={t('connection.create.placeholder.password')} autoComplete="new-password" />
      </Form.Item>
    </>
  );
};

export default JdbcConnectionForm;
