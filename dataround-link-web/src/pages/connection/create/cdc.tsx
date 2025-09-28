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
import { Form, Input, Popover } from "antd";
import { FC } from "react";
import { useTranslation } from 'react-i18next';

interface IProps {
}

const CDCConnectionForm: FC<IProps> = () => {
  const { t } = useTranslation();

  return (
    <>
      <Form.Item name="url" label={t('connection.create.form.jdbcUrl')} rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="user" label={t('connection.create.form.username')} rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="passwd" label={t('connection.create.form.password')} rules={[{ required: true }]}>
        <Input.Password />
      </Form.Item>
      <Form.Item name="timezone" label={t('connection.create.form.timezone')}>
        <Input placeholder="UTC+8" />
      </Form.Item>
    </>
  );
};

export default CDCConnectionForm;
