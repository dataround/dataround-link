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
import { Form, Input } from "antd";
import { FC } from "react";
import { useTranslation } from 'react-i18next';

interface IProps {
}

const KafkaConnectionForm: FC<IProps> = () => {
  const { t } = useTranslation();

  return (
    <>
      <Form.Item name="broker" label={t('connection.create.form.broker')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.broker')} />
      </Form.Item>
    </>
  );
};

export default KafkaConnectionForm;
