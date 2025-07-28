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
import { Form, Input, Select } from "antd";
import { FC } from "react";
import { useTranslation } from 'react-i18next';

const { Option } = Select;

interface IProps {
}

const S3ConnectionForm: FC<IProps> = () => {
  const { t } = useTranslation();

  return (
    <>
      <Form.Item name="endpoint" label={t('connection.create.form.endpoint')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.endpoint')} />
      </Form.Item>
      <Form.Item name="accessKey" label={t('connection.create.form.accessKey')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.accessKey')} />
      </Form.Item>
      <Form.Item name="secretKey" label={t('connection.create.form.secretKey')} rules={[{ required: true }]}>
        <Input.Password placeholder={t('connection.create.placeholder.secretKey')} />
      </Form.Item>
      <Form.Item name="bucket" label={t('connection.create.form.bucket')} rules={[{ required: true }]}>
        <Input placeholder={t('connection.create.placeholder.bucket')} />
      </Form.Item>
      <Form.Item name="region" label={t('connection.create.form.region')}>
         <Select 
           placeholder={t('connection.create.placeholder.region')} 
           allowClear
           showSearch
           mode="tags"
           filterOption={false}
           maxTagCount={1}
         >
           {/* AWS Regions */}
           <Option value="us-east-1">AWS US East (N. Virginia) - us-east-1</Option>
           <Option value="us-east-2">AWS US East (Ohio) - us-east-2</Option>
           <Option value="us-west-1">AWS US West (N. California) - us-west-1</Option>
           <Option value="us-west-2">AWS US West (Oregon) - us-west-2</Option>
           <Option value="eu-west-1">AWS Europe (Ireland) - eu-west-1</Option>
           <Option value="eu-west-2">AWS Europe (London) - eu-west-2</Option>
           <Option value="eu-west-3">AWS Europe (Paris) - eu-west-3</Option>
           <Option value="eu-central-1">AWS Europe (Frankfurt) - eu-central-1</Option>
           <Option value="ap-northeast-1">AWS Asia Pacific (Tokyo) - ap-northeast-1</Option>
           <Option value="ap-northeast-2">AWS Asia Pacific (Seoul) - ap-northeast-2</Option>
           <Option value="ap-southeast-1">AWS Asia Pacific (Singapore) - ap-southeast-1</Option>
           <Option value="ap-southeast-2">AWS Asia Pacific (Sydney) - ap-southeast-2</Option>
           <Option value="ap-south-1">AWS Asia Pacific (Mumbai) - ap-south-1</Option>
           <Option value="cn-north-1">AWS China (Beijing) - cn-north-1</Option>
           <Option value="cn-northwest-1">AWS China (Ningxia) - cn-northwest-1</Option>
           
           {/* aliyun OSS */}
           <Option value="oss-cn-hangzhou">aliyun oss-cn-hangzhou</Option>
           <Option value="oss-cn-shanghai">aliyun oss-cn-shanghai</Option>
           <Option value="oss-cn-beijing">aliyun oss-cn-beijing</Option>
           <Option value="oss-cn-shenzhen">aliyun oss-cn-shenzhen</Option>
           <Option value="oss-cn-guangzhou">aliyun oss-cn-guangzhou</Option>
           
           {/* tencent COS */}
           <Option value="ap-beijing">tencent ap-beijing</Option>
           <Option value="ap-shanghai">tencent ap-shanghai</Option>
           <Option value="ap-guangzhou">tencent ap-guangzhou</Option>
           <Option value="ap-chengdu">tencent ap-chengdu</Option>
           
           {/* MinIO default */}
           <Option value="minio">MinIO (default) - minio</Option>
         </Select>
      </Form.Item>
      <Form.Item name="pathStyleAccess" label={t('connection.create.form.pathStyleAccess')}>
        <Select placeholder={t('connection.create.placeholder.pathStyleAccess')} defaultValue={false}>
          <Option value={true}>{t('connection.create.option.enabled')}</Option>
          <Option value={false}>{t('connection.create.option.disabled')}</Option>
        </Select>
      </Form.Item>
    </>
  );
};

export default S3ConnectionForm; 