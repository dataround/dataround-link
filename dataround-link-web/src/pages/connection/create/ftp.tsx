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
import { Form, Input, Popover } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import { FC } from "react";
import { useTranslation } from 'react-i18next';

interface IProps {
}

const FtpConnectionForm: FC<IProps> = () => {
  const { t } = useTranslation();

  return (
    <>
      <Form.Item name="host" label={t('connection.create.form.host')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.host')} />
      </Form.Item>
      <Form.Item name="port" label={t('connection.create.form.port')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.port')} />
      </Form.Item>
      <Form.Item name="user" label={t('connection.create.form.username')} rules={[{ required: false }]}>
        <Input 
          placeholder={t('connection.create.placeholder.username')} 
          autoComplete="off"
          suffix={
            <Popover 
              content={t('connection.create.placeholder.usernameTip')} 
              title={t('common.tip')}
            >
              <InfoCircleOutlined style={{ color: '#1890ff' }} />
            </Popover>
          }
        />
      </Form.Item>
      <Form.Item name="passwd" label={t('connection.create.form.password')} rules={[{ required: false }]}>
        <Input.Password 
          placeholder={t('connection.create.placeholder.password')} 
          autoComplete="new-password"
        />
      </Form.Item>
      <Form.Item name="timeout" label={t('connection.create.form.timeout')} rules={[{ required: false }]}>
        <Input placeholder={t('connection.create.placeholder.timeout')} />
      </Form.Item>
    </>
  );
};

export default FtpConnectionForm;
