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
import { UploadOutlined } from "@ant-design/icons";
import { Form, Input, Popover, Upload, Button } from "antd";
import { FC } from "react";
import { useTranslation } from 'react-i18next';

interface IProps {
}

const HiveConnectionForm: FC<IProps> = () => {
  const { t } = useTranslation();

  return (
    <>
      <Form.Item name="metastoreUri" label={t('connection.create.form.metastoreUri')} rules={[{ required: true }]}>
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
};

export default HiveConnectionForm;
