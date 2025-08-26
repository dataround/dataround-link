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
import { DeleteOutlined, EditOutlined, InfoCircleOutlined, PlayCircleOutlined } from "@ant-design/icons";
import {
  Button, Card, Checkbox, Col, Form, Input, Popconfirm, Popover, Row, Select, Space,
  Table,
  TableProps,
  Tabs,
  TabsProps,
  TreeSelect,
  message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { deleteJob, executeJob, getJobId, getJobList } from "../../api/job";
import useRequest from "../../hooks/useRequest";
import { JOB_TYPE_BATCH, JOB_TYPE_FILESYNC, JOB_TYPE_STREAM, jobStore } from "../../store";
import { formatConnector, getConnector } from "../../api/connection";

interface IProps { }

interface DataType {
  key: string;
  name: string;
  jobSource: string;
  jobTarget: string;
  scheduleType: string;
  cron: string;
  startTime: string;
  endTime: string;
  updateUser: string;
  updateTime: string;
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [refresh, setRefresh] = useState<number>(0);
  const [tabData, setTabData] = useState<DataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  // retrive jobType from path
  const jobType = window.location.pathname.includes("/batch") ? JOB_TYPE_BATCH : window.location.pathname.includes("/stream") ? JOB_TYPE_STREAM : JOB_TYPE_FILESYNC;
  // clean all store
  jobStore.cleanAll();

  const columns: TableProps<DataType>["columns"] = [
    {
      title: t('job.list.jobId'),
      dataIndex: "key",
      key: "key"
    },
    {
      title: t('job.list.jobName'),
      dataIndex: "name",
      key: "name"
    },
    {
      title: t('job.list.source'),
      dataIndex: "jobSource",
      key: "jobSource",
    },
    {
      title: t('job.list.target'),
      dataIndex: "jobTarget",
      key: "jobTarget",
    },
    {
      title: t('job.list.scheduleType'),
      key: "scheduleType",
      render: (_, record) => (
        record.scheduleType == "1" ? t('job.list.scheduleTypes.immediate') : record.scheduleType == "2" ? (
          <Popover content={
            <div>
              {t('job.list.periodicInfo.cron')}: {record.cron}<br/>
              {t('job.list.periodicInfo.validPeriod')}: {record.startTime} - {record.endTime ? record.endTime : t('job.list.periodicInfo.infinite')}
            </div>
          }>
            <Space size={0}>
              {t('job.list.scheduleTypes.periodic')}
              <Button type="link" style={{ padding: 0 }}><InfoCircleOutlined /></Button>
            </Space>
          </Popover>
        ) : t('job.list.scheduleTypes.none')
      )
    },
    {
      title: t('job.list.updateUser'),
      dataIndex: "updateUser",
      key: "updateUser",
    },
    {
      title: t('job.list.updateTime'),
      key: "updateTime",
      dataIndex: "updateTime",
    },
    {
      title: t('job.list.operation'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          {record.scheduleType != "2" && (
            <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleExecute(record)}>
              <PlayCircleOutlined />{t('job.list.execute')}
            </Button>
          )}
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}>
            <EditOutlined />{t('job.list.edit')}
          </Button>
          <Popconfirm title={t('job.list.deleteConfirm')} onConfirm={() => handleDelete(record)}>
            <Button type="link" style={{ padding: 0, gap: '4px' }}>
              <DeleteOutlined />{t('job.list.delete')}
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const handleExecute = (record: DataType) => {
    execRequest.caller(record.key);
  };

  const handleEdit = (record: DataType) => {
    detailRequest.caller(record.key);
  };

  const handleDelete = (record: DataType) => {
    deleteRequest.caller(record.key).then(() => {
      setRefresh(Math.random);
    });
  };

  const formatData = (res: any) => {
    setPageSize(res.size);
    setTotalCount(res.total);
    const records = res.records;
    const jobs: DataType[] = [];
    Object.keys(records).forEach((i) => {
      jobs.push({
        key: records[i].id,
        name: records[i].name,
        jobSource: records[i].sourceConnectionName,
        jobTarget: records[i].targetConnectionName,
        scheduleType: records[i].scheduleType,
        cron: records[i].cron,
        startTime: records[i].startTime,
        endTime: records[i].endTime,
        updateUser: records[i].updateUserName,
        updateTime: records[i].updateTime
      });
    });
    setTabData(jobs);
    return jobs;
  };
  const listRequest = useRequest(getJobList, {
    wrapperFun: formatData,
  });

  useEffect(() => {
    reqSourceConnector.caller({ supportSource: true, isStream: jobType == JOB_TYPE_STREAM, fileType: jobType == JOB_TYPE_FILESYNC });
    reqTargetConnector.caller({ supportSink: true, fileType: jobType == JOB_TYPE_FILESYNC });
  }, []);

  useEffect(() => {
    listRequest.caller({ size: pageSize, jobType: jobType });
  }, [refresh]);

  const execRequest = useRequest(executeJob, {
    wrapperFun: (res: any) => {
      message.success(t('job.list.executeSuccess'));
    },
  });

  const detailRequest = useRequest(getJobId, {
    wrapperFun: (res: any) => {
      jobStore.setId(res.id);
      jobStore.setName(res.name);
      jobStore.setDescription(res.description);
      jobStore.setJobType(res.jobType);
      jobStore.setScheduleType(res.scheduleType);
      jobStore.setStartTime(res.startTime);
      jobStore.setEndTime(res.endTime);
      jobStore.setCron(res.cron);

      jobStore.setSourceConnId(res.sourceConnId);
      jobStore.setTargetConnId(res.targetConnId);
      jobStore.setSourceDbName(res.sourceDbName);
      jobStore.setTargetDbName(res.targetDbName);
      jobStore.setTableMapping(res.tableMapping);
      if (jobType == JOB_TYPE_FILESYNC) {
        navigate('/fileSync/create?id=' + res.id);
      } else {
        navigate('/batch/job/create?id=' + res.id);
      }
    },
  });

  const deleteRequest = useRequest(deleteJob, {
    wrapperFun: (res: any) => {
      message.success(t('job.list.deleteSuccess'));
    },
  });

  const items: TabsProps["items"] = [
    {
      key: "tabBatch",
      label: t('job.list.title')
    },
  ];

  const scheduleTypeOptions = [
    {
      label: t('job.list.scheduleTypes.immediate'),
      value: "1",
    },
    {
      label: t('job.list.scheduleTypes.periodic'),
      value: "2",
    },
    {
      label: t('job.list.scheduleTypes.none'),
      value: "3",
    },
  ];
  if (jobType !== JOB_TYPE_BATCH) {
    scheduleTypeOptions.splice(1, 1);
  }

  // request connector
  const reqSourceConnector = useRequest(getConnector, {
    wrapperFun: formatConnector
  });

  const reqTargetConnector = useRequest(getConnector, {
    wrapperFun: formatConnector
  });

  const newBatchJob = () => {
    if (jobType == JOB_TYPE_BATCH) {
      navigate(`/batch/job/create?jobType=${jobType}`);
    } else if (jobType == JOB_TYPE_STREAM) {
      navigate(`/batch/job/create?jobType=${jobType}`);
    } else if (jobType == JOB_TYPE_FILESYNC) {
      navigate(`/fileSync/create?jobType=${jobType}`);
    }
  };

  const onPageChange = (current: number, size: number) => {
    setPageSize(size);
    listRequest.caller({ current: current, size: size, jobType: jobType });
  };

  const onFinish = (values: any) => {
    const filterdValues = Object.keys(values).reduce((acc, key) => {
      if (values[key] !== undefined && values[key] !== '') {
        acc[key] = values[key];
      }
      return acc;
    }, {} as any);
    listRequest.caller({ ...filterdValues, size: pageSize, jobType: jobType });
  };

  return (
    <>
      <Card style={{ marginBottom: 20 }} bodyStyle={{ padding: '10px' }}>
        <Form
          form={form}
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 16 }}
          onFinish={onFinish}>
          <Row gutter={[16, 2]}>
            <Col span={6}>
              <Form.Item label={t('job.list.jobId')} name="id" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('job.list.jobName')} name="name" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={7}>
              <Form.Item label={t('job.list.scheduleType')} labelCol={{ span: 12}} name="scheduleType" style={{ marginBottom: 5, textAlign: 'left' }}>
                <Select options={scheduleTypeOptions} placeholder={t('job.list.scheduleType')} allowClear/>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={[16, 2]}>
            <Col span={6}>
              <Form.Item label={t('job.list.source')} name="jobSource" style={{ marginBottom: 5 }}>
                <TreeSelect
                  showSearch
                  style={{ width: '100%', textAlign: 'left' }}
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                  placeholder="Please select"
                  allowClear
                  treeDefaultExpandAll
                  treeData={reqSourceConnector.data}
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('job.list.target')} name="jobTarget" style={{ marginBottom: 5 }}>
                <TreeSelect
                  showSearch
                  style={{ width: '100%', textAlign: 'left' }}
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                  placeholder="Please select"
                  allowClear
                  treeDefaultExpandAll
                  treeData={reqTargetConnector.data}
                />
              </Form.Item>
            </Col>
            <Col span={7}>
              {
                (jobType == JOB_TYPE_BATCH) && (
                  <Form.Item
                    label={<>
                      {t('job.list.waitingExec.label')}
                      <Popover content={t('job.list.waitingExec.tooltip')}>
                        <Button type="link" style={{ padding: 0 }}><InfoCircleOutlined /></Button>
                      </Popover>
                    </>}
                    labelCol={{ span: 12}} name="waitingExec" style={{ marginBottom: 5, textAlign: 'left' }}>
                    <Checkbox name="waitingExec" onChange={(e) => form.setFieldValue("waitingExec", e.target.checked)} />
                  </Form.Item>
                )
              }
            </Col>
          </Row>
          <Row gutter={[16, 2]}>
            <Col span={16}></Col>
            <Col span={2} style={{ textAlign: 'right' }}>
              <Button type="primary" htmlType="submit">{t('common.search')}</Button>
            </Col>
          </Row>
        </Form>
      </Card>
      <Tabs defaultActiveKey="tabBatch" items={items} tabBarExtraContent={
        <Button type="primary" htmlType="submit" onClick={newBatchJob}>{jobType == JOB_TYPE_BATCH ? t('job.list.newBatchJob') : jobType == JOB_TYPE_STREAM ? t('job.list.newStreamJob') : t('job.list.newFileSyncJob')}</Button>
      }
      />
      <Table size="small" columns={columns} dataSource={tabData} pagination={{ pageSize: pageSize, total: totalCount, onChange: onPageChange }} />
    </>
  );
};

const BatchJob = memo(S);

export default BatchJob;