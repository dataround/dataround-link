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
import {
  Col,
  DatePicker,
  DatePickerProps,
  Form,
  Input,
  Radio,
  RadioChangeEvent,
  Row,
  Select,
  TimePicker,
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import dayjs, { Dayjs } from "dayjs";
import { CRON_PER_DAY, CRON_PER_HOUR, CRON_PER_MIN, CRON_PER_MONTH, CRON_PER_WEEK, JOB_TYPE_BATCH, jobStore } from "../../../store";
import CronExpression from "../../../components/cron";
import { useTranslation } from 'react-i18next';

interface IProps {
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const jobType = jobStore.jobType;
  const [name, setName] = useState<string>(jobStore.name);
  const [description, setDescription] = useState<string>(jobStore.description);
  const [scheduleType, setScheduleType] = useState(jobStore.scheduleType);
  const [cron, setCron] = useState<string>(jobStore.cron);

  const [startTime, setStartTime] = useState<string>(jobStore.startTime);
  const [endTime, setEndTime] = useState<string>(jobStore.endTime);

  const onScheduleTypeChange = (e: RadioChangeEvent) => {
    setScheduleType(e.target.value);
  };

  const onStartTimeChange: DatePickerProps['onChange'] = (date, dateString) => {
    setStartTime(dateString + ":00");
  };

  const onEndTimeChange: DatePickerProps['onChange'] = (date, dateString) => {
    setEndTime(dateString + ":00");
  };

  const disabledEndTime = (current: Dayjs) => {
    if (!startTime) {
      return false;
    }
    // If the current time is less than startTime, do disable
    return current.valueOf() < dayjs(startTime).valueOf();
  };

  useEffect(() => {
    jobStore.setName(name);
    jobStore.setDescription(description);
    jobStore.setScheduleType(scheduleType);
    jobStore.setStartTime(startTime);
  }, [name, description, scheduleType, cron, startTime, endTime]);

  return (
    <>
      <Form
        form={form}
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 12 }}
        style={{ marginTop: 35 }}>
        <Form.Item 
          name="name" 
          label={t('job.edit.save.form.name')} 
          rules={[{ required: true, message: t('job.edit.save.message.nameRequired') }]}>
          <Input 
            placeholder={t('job.edit.save.placeholder.name')} 
            defaultValue={name} 
            onChange={(e) => setName(e.target.value)}>
          </Input>
        </Form.Item>
        <Form.Item 
          name="description" 
          label={t('job.edit.save.form.description')}>
          <Input.TextArea 
            placeholder={t('job.edit.save.placeholder.description')} 
            defaultValue={description} 
            onChange={(e) => setDescription(e.target.value)}>
          </Input.TextArea>
        </Form.Item>
        <Form.Item 
          name="scheduleType" 
          label={t('job.edit.save.form.scheduleType')} 
          rules={[{ required: true, message: t('job.edit.save.message.scheduleTypeRequired') }]}>
          <Radio.Group onChange={onScheduleTypeChange} defaultValue={scheduleType}>
            <Radio value={1}>{t('job.edit.save.scheduleType.immediate')}</Radio>
            {jobType === JOB_TYPE_BATCH && (
              <Radio value={2}>{t('job.edit.save.scheduleType.periodic')}</Radio>
            )}            
            <Radio value={3}>{t('job.edit.save.scheduleType.none')}</Radio>
          </Radio.Group>
        </Form.Item>
        {scheduleType === 2 && (<CronExpression cron={cron}></CronExpression>)}
        <Form.Item name="expire" label={t('job.edit.save.form.validPeriod')}>
          <DatePicker 
            name="startTime" 
            showTime={{ format: 'HH:mm' }} 
            format="YYYY-MM-DD HH:mm" 
            defaultValue={startTime ? dayjs(startTime) : null} 
            onChange={onStartTimeChange} 
          />
          &nbsp;{t('job.edit.save.validPeriod.to')}&nbsp;
          <DatePicker 
            name="endTime" 
            showTime={{ format: 'HH:mm' }} 
            format="YYYY-MM-DD HH:mm" 
            defaultValue={endTime ? dayjs(endTime) : null} 
            onChange={onEndTimeChange} 
            disabledDate={disabledEndTime} 
          />
        </Form.Item>
      </Form>
    </>
  );
};

const StepSave = memo(S);

export default StepSave;