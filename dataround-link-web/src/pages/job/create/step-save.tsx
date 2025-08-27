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
import { DatePicker, DatePickerProps, Form, Input, Radio, RadioChangeEvent } from "antd";
import { FC, memo, useEffect, useState, forwardRef, useImperativeHandle } from "react";
import CronExpression from "../../../components/cron";
import { JOB_TYPE_BATCH } from "../../../store";
import dayjs, { Dayjs } from "dayjs";
import { useTranslation } from 'react-i18next';
import { JobFormData, StepRef } from './index';

interface IProps {
  data: JobFormData;
  onDataChange: (updates: Partial<JobFormData>) => void;
}

const S = forwardRef<StepRef, IProps>((props, ref) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const { data, onDataChange } = props;
  const jobType = data.jobType;

  // expose validateFields and getFieldsValue
  useImperativeHandle(ref, () => ({
    validateFields: async () => {
      try {
        await form.validateFields();
        const formValues = form.getFieldsValue();
        onDataChange({
          name: formValues.name,
          description: formValues.description,
          scheduleType: localData.scheduleType,
          startTime: localData.startTime,
          endTime: localData.endTime,
          cron: localData.cron
        });
        return true;
      } catch (error) {
        return false;
      }
    },
    getFieldsValue: () => form.getFieldsValue()
  }));

  // sync external data to form
  useEffect(() => {
    const formData = {
      name: data.name,
      description: data.description,
      scheduleType: data.scheduleType,
      startTime: data.startTime ? dayjs(data.startTime) : undefined,
      endTime: data.endTime ? dayjs(data.endTime) : undefined
    };
    form.setFieldsValue(formData);

    setLocalData({
      scheduleType: data.scheduleType,
      startTime: data.startTime,
      endTime: data.endTime,
      cron: data.cron
    });
  }, [data, form]);

  const [localData, setLocalData] = useState({
    scheduleType: data.scheduleType,
    startTime: data.startTime,
    endTime: data.endTime,
    cron: data.cron
  });

  const onScheduleTypeChange = (e: RadioChangeEvent) => {
    const scheduleType = e.target.value;
    setLocalData(prev => ({ ...prev, scheduleType }));
  };

  const onStartTimeChange: DatePickerProps['onChange'] = (date, dateString) => {
    const startTime = dateString ? dateString + ":00" : '';
    setLocalData(prev => ({ ...prev, startTime }));
  };

  const onEndTimeChange: DatePickerProps['onChange'] = (date, dateString) => {
    const endTime = dateString ? dateString + ":00" : '';
    setLocalData(prev => ({ ...prev, endTime }));
  };

  const disabledEndTime = (current: any) => {
    if (!localData.startTime) { return false; }
    return current && current.valueOf() < dayjs(localData.startTime).valueOf();
  };

  return (
    <>
      <Form
        form={form}
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 12 }}
        style={{ marginTop: 35 }}>
        <Form.Item name="name" label={t('job.edit.save.form.name')}
          rules={[{ required: true, message: t('job.edit.save.message.nameRequired') }]}>
          <Input placeholder={t('job.edit.save.placeholder.name')} />
        </Form.Item>
        <Form.Item name="description" label={t('job.edit.save.form.description')}>
          <Input.TextArea placeholder={t('job.edit.save.placeholder.description')} />
        </Form.Item>
        <Form.Item name="scheduleType" label={t('job.edit.save.form.scheduleType')}
          rules={[{ required: true, message: t('job.edit.save.message.scheduleTypeRequired') }]}>
          <Radio.Group onChange={onScheduleTypeChange} value={localData.scheduleType}>
            <Radio value={1}>{t('job.edit.save.scheduleType.immediate')}</Radio>
            {jobType === JOB_TYPE_BATCH && (<Radio value={2}>{t('job.edit.save.scheduleType.periodic')}</Radio>)}
            <Radio value={3}>{t('job.edit.save.scheduleType.none')}</Radio>
          </Radio.Group>
        </Form.Item>
        {localData.scheduleType === 2 && (<CronExpression cron={localData.cron || ''}
          onChange={(cron: string) => setLocalData(prev => ({ ...prev, cron }))} />)}
        <Form.Item name="expire" label={t('job.edit.save.form.validPeriod')}>
          <DatePicker name="startTime" placeholder={t('job.edit.save.placeholder.startTime')}
            showTime format="YYYY-MM-DD HH:mm" onChange={onStartTimeChange} style={{ marginRight: 8 }} />
          <DatePicker name="endTime" placeholder={t('job.edit.save.placeholder.endTime')}
            showTime format="YYYY-MM-DD HH:mm" onChange={onEndTimeChange} disabledDate={disabledEndTime} />
        </Form.Item>
      </Form>
    </>
  );
});

const StepSave = memo(S);

export default StepSave;