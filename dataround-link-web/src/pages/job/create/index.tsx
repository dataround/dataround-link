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
  Button, Form, Steps, message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { getJobId, saveJob } from "../../../api/job";
import useRequest from "../../../hooks/useRequest";
import { JOB_TYPE_BATCH, jobStore } from "../../../store";
import "./index.less";
import StepMapping from "./step-mapping";
import StepSave from "./step-save";
import StepSource from "./step-source";
import { useNavigate } from "react-router-dom";
import { useTranslation } from 'react-i18next';

interface IProps { }

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [current, setCurrent] = useState(0);
  const navigate = useNavigate();
  // retrieve the value of the 'jobType' parameter from the URL
  var urlParams = new URLSearchParams(window.location.search);
  const jobId = urlParams.get("id");
  let jobTypeParam = urlParams.get('jobType');
  jobTypeParam = jobTypeParam == null ? "" : jobTypeParam;
  const jobType = urlParams.get('jobType') ? parseInt(jobTypeParam) : jobStore.jobType;
  // StepSource need access jobType from jobStore
  jobStore.jobType = jobType;

  const steps = [
    {
      title: t('job.edit.steps.source'),
      content: <StepSource />,
      description: ''
    },
    {
      title: t('job.edit.steps.mapping'),
      content: <StepMapping />,
    },
    {
      title: t('job.edit.steps.save'),
      content: <StepSave />,
    },
  ];

  const next = () => {
    setCurrent(current + 1);
  };

  const prev = () => {
    setCurrent(current - 1);
  };

  const onSaveJob = ()=> {
    const params:any = {};
    params.id = jobStore.id;
    params.name = jobStore.name;
    params.description = jobStore.description;
    params.jobType = jobType;
    params.scheduleType = jobStore.scheduleType;
    params.cron = jobStore.cron;
    params.startTime = jobStore.startTime;
    params.endTime = jobStore.endTime;

    params.sourceConnId = jobStore.sourceConnId;
    params.targetConnId = jobStore.targetConnId;
    params.sourceDbName = jobStore.sourceDbName;
    params.targetDbName = jobStore.targetDbName;
    params.tableMapping = jobStore.tableMapping;
    reqSaveJob.caller(params);
  }

  const reqSaveJob = useRequest(saveJob, {
    wrapperFun: (res: any) => {
      message.success(t('job.edit.message.saveSuccess'))
      navigate("/" + (jobType == JOB_TYPE_BATCH ? "batch" : "stream") + "/job");
    }
  });

  const items = steps.map((item) => ({ key: item.title, title: item.title, description: item.description }));

  const contentStyle: React.CSSProperties = {
    textAlign: 'left',
    marginTop: 16,
    marginLeft: 20,
  };
  return (
    <>
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
    </>
  );
};

const JobForm = memo(S);

export default JobForm;