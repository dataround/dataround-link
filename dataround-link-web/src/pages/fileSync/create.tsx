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
import { SaveOutlined } from "@ant-design/icons";
import { Button, DatePicker, DatePickerProps, Form, Input, message, Radio, RadioChangeEvent, Select, Space, Switch, Tabs } from "antd";
import dayjs, { Dayjs } from "dayjs";
import { FC, memo, useEffect, useState } from "react";
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from "react-router-dom";
import { getConnections } from "../../api/connection";
import { getJobId, saveJob } from "../../api/job";
import CronExpression from "../../components/cron";
import useRequest from "../../hooks/useRequest";
import "./index.less";

const { Option } = Select;
const { TextArea } = Input;

interface IProps {
}

const FileSyncCreate: FC<IProps> = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const [id] = useState<string>(searchParams.get("id") as string);
  const [form] = Form.useForm();
  const [scheduleType, setScheduleType] = useState();
  const [cron, setCron] = useState<string>('0 0 2 * * ?');

  const onScheduleTypeChange = (e: RadioChangeEvent) => {
    setScheduleType(e.target.value);
  };

  const onStartTimeChange: DatePickerProps['onChange'] = (date, dateString) => {
    form.setFieldsValue({ startTime: dateString ? dayjs(dateString + ":00") : null });
  };

  const onEndTimeChange: DatePickerProps['onChange'] = (date, dateString) => {
    form.setFieldsValue({ endTime: dateString ? dayjs(dateString + ":00") : null });
  };

  const disabledEndTime = (current: any) => {
    const startTime = form.getFieldValue('startTime');
    if (!startTime) {
      return false;
    }
    return current.valueOf() < dayjs(startTime).valueOf();
  };

  // get connection list
  const reqConnections = useRequest(getConnections);

  // get file sync job detail
  const reqJobDetail = useRequest(getJobId);

  useEffect(() => {
    reqConnections.caller({"types":["File"]}).then((res: any) => {
      const options = res.map((conn: any) => ({ value: conn.id, label: `${conn.name} (${conn.connector})` }));
      form.setFieldsValue({
        sourceConnectionOptions: options,
        targetConnectionOptions: options
      });
    });
    if (id) {
      reqJobDetail.caller(id).then((res: any) => {
        res.startTime = res.startTime ? dayjs(res.startTime) : null;
        res.endTime = res.endTime ? dayjs(res.endTime) : null;
        form.setFieldsValue(res);
      });
    }
  }, [id]);

  const handleSave = async (values: any) => {
    try {
      console.log('values', values);
      // convert iso time to local time
      const startTime = values.startTime ? values.startTime.format('YYYY-MM-DD HH:mm:ss') : null;
      const endTime = values.endTime ? values.endTime.format('YYYY-MM-DD HH:mm:ss') : null;
      await saveJob({ ...values, startTime, endTime, id, jobType: 3 });
      message.success(t('fileSync.message.saveSuccess'));
      navigate("/fileSync/job");
    } catch (error) {
      message.error(t('fileSync.message.saveFailed'));
    }
  };

  const handleCancel = () => {
    navigate("/fileSync/job");
  };

  return (
    <div className="file-sync-create">
      <Form
        form={form}
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 10 }}
        onFinish={handleSave}
        initialValues={{
          includeSubdirectories: true,
          enabled: true
        }}
      >
        <Tabs defaultActiveKey="tabList" items={[{ key: "tabSource", label: t('fileSync.create.sourceConfig') }]} />
        <Form.Item
          name="sourceConnId"
          label={t('fileSync.create.form.sourceConnection')}
          rules={[{ required: true, message: t('fileSync.create.message.sourceConnectionRequired') }]}
        >
          <Select
            placeholder={t('fileSync.create.placeholder.selectSourceConnection')}
            showSearch
            filterOption={(input, option) =>
              (option?.children as unknown as string)?.toLowerCase().includes(input.toLowerCase())
            }
          >
            {(form.getFieldValue('sourceConnectionOptions') || []).map((item: any) => (
              <Option key={item.value} value={item.value}>{item.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="sourcePath"
          label={t('fileSync.create.form.sourcePath')}
          rules={[{ required: true, message: t('fileSync.create.message.sourcePathRequired') }]}
        >
          <Input placeholder={t('fileSync.create.placeholder.sourcePath')} />
        </Form.Item>
        <Form.Item
          name="filePattern"
          label={t('fileSync.create.form.filePattern')}
          rules={[{ required: true, message: t('fileSync.create.message.filePatternRequired') }]}
        >
          <Input placeholder={t('fileSync.create.placeholder.filePattern')} />
        </Form.Item>
        <Form.Item
          name="includeSubdirectories"
          label={t('fileSync.create.form.includeSubdirectories')}
          valuePropName="checked"
        >
          <Switch />
        </Form.Item>

        <Tabs defaultActiveKey="tabList" items={[{ key: "tabTarget", label: t('fileSync.create.targetConfig') }]} />
        <Form.Item
          name="targetConnId"
          label={t('fileSync.create.form.targetConnection')}
          rules={[{ required: true, message: t('fileSync.create.message.targetConnectionRequired') }]}
        >
          <Select
            placeholder={t('fileSync.create.placeholder.selectTargetConnection')}
            showSearch
            filterOption={(input, option) =>
              (option?.children as unknown as string)?.toLowerCase().includes(input.toLowerCase())
            }
          >
            {(form.getFieldValue('targetConnectionOptions') || []).map((item: any) => (
              <Option key={item.value} value={item.value}>{item.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="targetPath"
          label={t('fileSync.create.form.targetPath')}
          rules={[{ required: true, message: t('fileSync.create.message.targetPathRequired') }]}
        >
          <Input placeholder={t('fileSync.create.placeholder.targetPath')} />
        </Form.Item>

        <Tabs defaultActiveKey="tabList" items={[{ key: "tabJob", label: t('fileSync.create.jobConfig') }]} />
        <Form.Item
          name="name"
          label={t('job.edit.save.form.name')}
          rules={[{ required: true, message: t('job.edit.save.message.nameRequired') }]}
        >
          <Input placeholder={t('job.edit.save.placeholder.name')} />
        </Form.Item>

        <Form.Item
          name="description"
          label={t('job.edit.save.form.description')}
        >
          <Input.TextArea placeholder={t('job.edit.save.placeholder.description')} />
        </Form.Item>

        <Form.Item
          name="scheduleType"
          label={t('job.edit.save.form.scheduleType')}
          rules={[{ required: true, message: t('job.edit.save.message.scheduleTypeRequired') }]}
        >
          <Radio.Group onChange={onScheduleTypeChange} defaultValue={scheduleType}>
            <Radio value={1}>{t('job.edit.save.scheduleType.immediate')}</Radio>
            <Radio value={2}>{t('job.edit.save.scheduleType.periodic')}</Radio>
            <Radio value={3}>{t('job.edit.save.scheduleType.none')}</Radio>
          </Radio.Group>
        </Form.Item>

        {scheduleType === 2 && (
            <CronExpression 
              cron={cron || ''} 
              onChange={(cronValue) => {
                setCron(cronValue);
                form.setFieldValue('cron', cronValue);
              }}
            />
        )}

        <Form.Item
          name="expire"
          label={t('job.edit.save.form.validPeriod')}
        >
          <Space>
            <Form.Item name="startTime" noStyle>
              <DatePicker
                showTime={{ format: 'HH:mm' }}
                format="YYYY-MM-DD HH:mm"
                onChange={onStartTimeChange}
                value={form.getFieldValue('startTime') ? dayjs(form.getFieldValue('startTime')) : null}
              />
            </Form.Item>
            &nbsp;{t('job.edit.save.validPeriod.to')}&nbsp;
            <Form.Item name="endTime" noStyle>
              <DatePicker
                showTime={{ format: 'HH:mm' }}
                format="YYYY-MM-DD HH:mm"
                onChange={onEndTimeChange}
                value={form.getFieldValue('endTime') ? dayjs(form.getFieldValue('endTime')) : null}
                disabledDate={disabledEndTime}
              />
            </Form.Item>
          </Space>
        </Form.Item>

        <Space>
          <Button onClick={handleCancel}>
            {t('fileSync.create.button.cancel')}
          </Button>
          <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
            {t('fileSync.create.button.save')}
          </Button>
        </Space>
      </Form>
    </div>
  );
};

export default memo(FileSyncCreate); 