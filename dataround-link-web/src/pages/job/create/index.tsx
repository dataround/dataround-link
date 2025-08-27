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
import { Button, Form, Spin, Steps, message } from "antd";
import { FC, memo, useEffect, useState, useRef, useCallback } from "react";
import { getJobId, saveJob } from "../../../api/job";
import useRequest from "../../../hooks/useRequest";
import { JOB_TYPE_BATCH } from "../../../store";
import "./index.less";
import StepMapping from "./step-mapping";
import StepSave from "./step-save";
import StepSource from "./step-source";
import { useNavigate } from "react-router-dom";
import { useTranslation } from 'react-i18next';

interface IProps { }

// define form data structure
export interface JobFormData {
  id?: string;
  name?: string;
  description?: string;
  jobType?: number;
  scheduleType?: number;
  cron?: string;
  startTime?: string;
  endTime?: string;
  sourceConnId?: string;
  targetConnId?: string;
  sourceDbName?: string;
  targetDbName?: string;
  tableMapping?: any[];
}

export interface StepRef {
  validateFields: () => Promise<boolean>;
  getFieldsValue: () => any;
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [current, setCurrent] = useState(0);
  const navigate = useNavigate();
  const stepSourceRef = useRef<StepRef>(null);
  const stepMappingRef = useRef<StepRef>(null);
  const stepSaveRef = useRef<StepRef>(null);
  
  const urlParams = new URLSearchParams(window.location.search);
  const jobId = urlParams.get("id");
  const isEdit = jobId != null;
  const [isLoading, setIsLoading] = useState(isEdit);
  let jobTypeParam = urlParams.get('jobType');
  jobTypeParam = jobTypeParam == null ? "" : jobTypeParam;
  const jobType = urlParams.get('jobType') ? parseInt(jobTypeParam) : JOB_TYPE_BATCH;
  
  // use local state to manage form data
  const [formData, setFormData] = useState<JobFormData>({
    jobType: jobType,
    sourceConnId: '',
    sourceDbName: '',
    targetConnId: '',
    targetDbName: '',
    tableMapping: [],
    name: '',
    description: '',
    scheduleType: 1,
    cron: '',
    startTime: '',
    endTime: ''
  });

  // update form data (memoized to prevent infinite loops)
  const updateFormData = useCallback((updates: Partial<JobFormData>) => {
    console.log("updateFormData", updates);
    setFormData(prev => ({ ...prev, ...updates }));
  }, []);

  // get job detail in edit mode
  const reqJobDetail = useRequest(getJobId, {
    wrapperFun: (res: any) => {
      const jobDetail = res.data || res;
      const initData: JobFormData = {
        id: jobDetail.id,
        name: jobDetail.name,
        description: jobDetail.description,
        jobType: jobDetail.jobType,
        scheduleType: jobDetail.scheduleType,
        cron: jobDetail.cron,
        startTime: jobDetail.startTime,
        endTime: jobDetail.endTime,
        sourceConnId: jobDetail.sourceConnId,
        targetConnId: jobDetail.targetConnId,
        sourceDbName: jobDetail.sourceDbName,
        targetDbName: jobDetail.targetDbName,
        tableMapping: jobDetail.tableMapping || []
      };
      setFormData(initData);
    }
  });

  useEffect(() => {
    if (isEdit && jobId) {
      reqJobDetail.caller(jobId).then(() => {
        setIsLoading(false);
      });
    }
  }, [jobId, isEdit]);

  const steps = [
    { title: t('job.edit.steps.source'), content: <StepSource ref={stepSourceRef} data={formData} onDataChange={updateFormData} />, description: '' },
    { title: t('job.edit.steps.mapping'), content: <StepMapping ref={stepMappingRef} data={formData} onDataChange={updateFormData} /> },
    { title: t('job.edit.steps.save'), content: <StepSave ref={stepSaveRef} data={formData} onDataChange={updateFormData} /> }
  ];

  const next = async () => {
    try {
      const currentStepRef = [stepSourceRef, stepMappingRef, stepSaveRef][current];
      if (currentStepRef.current) {
        const isValid = await currentStepRef.current.validateFields();
        if (isValid) {
          setCurrent(current + 1);
        }
      } else {
        setCurrent(current + 1);
      }
    } catch (error) {
      console.error('Step validation failed:', error);
    }
  };

  const prev = () => {
    setCurrent(current - 1);
  };

  const onSaveJob = async () => {
    try {
      // validate last step
      if (stepSaveRef.current) {
        const isValid = await stepSaveRef.current.validateFields();
        if (!isValid) return;
      }
      
      const params: any = {
        id: formData.id,
        name: formData.name,
        description: formData.description,
        jobType: formData.jobType,
        scheduleType: formData.scheduleType,
        cron: formData.cron,
        startTime: formData.startTime,
        endTime: formData.endTime,
        sourceConnId: formData.sourceConnId,
        targetConnId: formData.targetConnId,
        sourceDbName: formData.sourceDbName,
        targetDbName: formData.targetDbName,
        tableMapping: formData.tableMapping,
      };
      reqSaveJob.caller(params);
    } catch (error) {
      console.error('Save validation failed:', error);
    }
  }

  const reqSaveJob = useRequest(saveJob, {
    wrapperFun: (res: any) => {
      message.success(t('job.edit.message.saveSuccess'))
      navigate("/" + (jobType == JOB_TYPE_BATCH ? "batch" : "stream") + "/job");
    }
  });

  const items = steps.map((item) => ({ key: item.title, title: item.title, description: item.description }));
  const contentStyle: React.CSSProperties = { textAlign: 'left', marginTop: 16, marginLeft: 20 };
  // when edit mode, make sure job detail request finished before render
  if (isLoading) {
    return (
      <div style={{ padding: '50px', textAlign: 'center' }}>
        <Spin size="large" />
        <div style={{ marginTop: 16 }}>{t('common.loading')}</div>
      </div>
    );
  }

  return (
    <Spin spinning={isLoading}>
      <Steps current={current} items={items} style={{ padding: "10px 20px 10px 20px" }} />
      <div style={contentStyle}>{steps[current].content}</div>
      <div style={{ marginTop: 0, lineHeight: 5 }}>
        {current > 0 && (
          <Button style={{ margin: '0 8px' }} onClick={() => prev()}>
            {t('job.edit.buttons.prev')}
          </Button>
        )}
        {current < steps.length - 1 && (
          <Button type="primary" onClick={() => next()}>
            {t('job.edit.buttons.next')}
          </Button>
        )}
        {current === steps.length - 1 && (
          <Button type="primary" onClick={onSaveJob}>
            {t('job.edit.buttons.save')}
          </Button>
        )}
      </div>
    </Spin>
  );
};

const JobForm = memo(S);

export default JobForm;