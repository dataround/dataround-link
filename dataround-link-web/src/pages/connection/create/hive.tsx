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
import { Form, Input, Upload, Button, message } from "antd";
import { FC } from "react";
import { useTranslation } from 'react-i18next';
import { uploadFile } from "../../../api/connection";
import { RcFile } from "antd/es/upload";

interface IProps {
  form?: any;
}

const HiveConnectionForm: FC<IProps> = ({form: parentForm }) => {
  const { t } = useTranslation();

  // Handle file upload and return the key for the uploaded file
  const handleFileUpload = async (file: RcFile, fieldName: string) => {
    try {
      // Upload the file and get the generated key from backend
      const response: any = await uploadFile(file);
      
      if (response.code === 200) {
        // Set the generated file key in the form field
        parentForm.setFieldValue(fieldName, response.data);
        message.success(t('connection.create.message.fileUploadSuccess'));
      } else {
        message.error(t('connection.create.message.fileUploadFailed'));
      }
    } catch (error) {
      message.error(t('connection.create.message.fileUploadFailed'));
    }
    
    // Prevent default upload behavior
    return false;
  };

  // Custom function to handle file list changes and only store the file key
  const handleFileListChange = (fieldName: string) => {
    // Return the file key that was set by handleFileUpload
    // This ensures we only send the key to the backend, not the entire file object
    return parentForm.getFieldValue(fieldName);
  };

  return (
    <>
      <Form.Item name="metastoreUri" label={t('connection.create.form.metastoreUri')} rules={[{ required: true }]}>
        <Input placeholder="thrift://127.0.0.1:9083" />
      </Form.Item>
      <Form.Item name="hdfsSite" label={t('connection.create.form.hdfsSite')} getValueFromEvent={() => handleFileListChange('hdfsSite')}>
        <Upload 
          beforeUpload={(file) => handleFileUpload(file, 'hdfsSite')}
          maxCount={1}
        >
          <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
        </Upload>
      </Form.Item>
      <Form.Item name="hiveSite" label={t('connection.create.form.hiveSite')} getValueFromEvent={() => handleFileListChange('hiveSite')}>
        <Upload 
          beforeUpload={(file) => handleFileUpload(file, 'hiveSite')}
          maxCount={1}
        >
          <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
        </Upload>
      </Form.Item>
      <Form.Item name="kerberosPrincipal" label={t('connection.create.form.kerberosPrincipal')}>
        <Input placeholder="kerberos_principal" />
      </Form.Item>
      <Form.Item name="kerberosKeytab" label={t('connection.create.form.kerberosKeytab')} getValueFromEvent={() => handleFileListChange('kerberosKeytab')}>
        <Upload 
          beforeUpload={(file) => handleFileUpload(file, 'kerberosKeytab')}
          maxCount={1}
        >
          <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
        </Upload>
      </Form.Item>
      <Form.Item name="kerberosKrb5Conf" label={t('connection.create.form.kerberosKrb5Conf')} getValueFromEvent={() => handleFileListChange('kerberosKrb5Conf')}>
        <Upload 
          beforeUpload={(file) => handleFileUpload(file, 'kerberosKrb5Conf')}
          maxCount={1}
        >
          <Button icon={<UploadOutlined />}>{t('connection.create.button.upload')}</Button>
        </Upload>
      </Form.Item>
    </>
  );
};

export default HiveConnectionForm;