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
  Button,
  Card, Col,
  DatePicker,
  Form,
  Input,
  Modal,
  Popconfirm, Row,
  Select,
  Space,
  Spin,
  Table,
  TableProps,
  Tabs,
  TabsProps, message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { deleteInstance, getInstanceList, stopInstance, getInstanceById } from "../../api/instance";
import useRequest from "../../hooks/useRequest";
import dayjs from "dayjs";
import { CheckCircleOutlined, ClockCircleOutlined, CloseCircleOutlined, DeleteOutlined, ExclamationCircleOutlined, EyeOutlined, PauseCircleOutlined, StopOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
// Using ES6 import syntax
import hljs from "highlight.js/lib/core";
import java from "highlight.js/lib/languages/java";
import "highlight.js/styles/default.css";
import { JOB_TYPE_BATCH, JOB_TYPE_FILESYNC, JOB_TYPE_STREAM } from "../../store";
import CommonTable from "../../components/common-table";
// Then register the languages you need
hljs.registerLanguage("java", java);

interface IProps { }

interface DataType {
  key: string;
  jobName: string;
  readCount: string;
  writeCount: string;
  readQps: string;
  writeQps: string;
  readBytes: string;
  writeBytes: string;
  status: number;
  logContent: string;
  startTime: string;
  endTime: string;
  duration: number | string;
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [tabData, setTabData] = useState<DataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [logContent, setLogContent] = useState("");
  const [logLoading, setLogLoading] = useState(false);
  // retrive jobType from path
  const jobType = window.location.pathname.includes("/batch") ? JOB_TYPE_BATCH : window.location.pathname.includes("/stream") ? JOB_TYPE_STREAM : JOB_TYPE_FILESYNC;

  const columns: TableProps<DataType>["columns"] = [
    {
      title: t('instance.table.instanceId'),
      dataIndex: "key",
      key: "key",
    },
    {
      title: t('instance.table.jobName'),
      dataIndex: "jobName",
      key: "jobName",
    },
    {
      title: t('instance.table.readCount'),
      dataIndex: "readCount",
      key: "readCount",
    },
    {
      title: t('instance.table.writeCount'),
      dataIndex: "writeCount",
      key: "writeCount",
    },
    {
      title: t('instance.table.readQps'),
      dataIndex: "readQps",
      key: "readQps",
    },
    {
      title: t('instance.table.writeQps'),
      dataIndex: "writeQps",
      key: "writeQps",
    },
    {
      title: t('instance.table.readBytes'),
      dataIndex: "readBytes",
      key: "readBytes",
    },
    {
      title: t('instance.table.writeBytes'),
      dataIndex: "writeBytes",
      key: "writeBytes",
    },
    {
      title: t('instance.table.status'),
      key: "status",
      render: (_, record) => (
        // 0:waiting, 1:submitted, 2:running, 3:success, 4:failure, 5:canceled
        record.status == 0 ? <><span style={{ color: "#999" }}><ClockCircleOutlined /> {t('instance.status.waiting')}</span></> :
          record.status == 1 ? <><span style={{ color: "#999" }}><ExclamationCircleOutlined /> {t('instance.status.submitted')}</span></> :
            record.status == 2 ? <><span style={{ color: "#1890ff" }}><PauseCircleOutlined spin /> {t('instance.status.running')}</span></> :
              record.status == 3 ? <><span style={{ color: "#00a854" }}><CheckCircleOutlined /> {t('instance.status.success')}</span></> :
                record.status == 4 ? <><span style={{ color: "#f50" }}><CloseCircleOutlined /> {t('instance.status.failure')}</span></> :
                  record.status == 5 ? <><span style={{ color: "#00a854" }}><CheckCircleOutlined /> {t('instance.status.canceled')}</span></> :
                    <span>{record.status}</span>
      ),
    },
    {
      title: t('instance.table.startTime'),
      key: "startTime",
      dataIndex: "startTime",
    },
    {
      title: t('instance.table.endTime'),
      key: "endTime",
      dataIndex: "endTime",
    },
    {
      title: t('instance.table.duration'),
      key: "duration",
      dataIndex: "duration",
    },
    {
      title: t('instance.table.operation'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          {record.logContent &&
            <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => viewLogContent(record)}><EyeOutlined />{t('instance.table.log')}</Button>
          }
          {
            record.status == 2 && (
              <Popconfirm title={t('instance.modal.stopConfirm')} onConfirm={() => handleStop(record)}>
                <Button type="link" style={{ padding: 0, gap: '4px' }}><StopOutlined />{t('instance.table.stop')}</Button>
              </Popconfirm>
            )
          }
          <Popconfirm title={t('instance.modal.deleteConfirm')} onConfirm={() => handleDelete(record)}>
            <Button type="link" style={{ padding: 0, gap: '4px' }}><DeleteOutlined />{t('instance.table.delete')}</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const viewLogContent = (record: DataType) => {
    // previous log content
    setLogContent(record.logContent || "");
    setIsModalOpen(true);
    // then request the latest log content
    setLogLoading(true);
    getInstanceById(record.key).then((res: any) => {
      setLogContent(res.data.logContent || "");
    }).catch((error: any) => {
      message.error(t('instance.message.fetchLogFailed'));
    }).finally(() => {
      setLogLoading(false);
    });
  };

  const deleteRequest = useRequest(deleteInstance, {
    wrapperFun: (res: any) => {
      message.success(t('instance.message.deleteSuccess'));
    },
  });

  const handleDelete = (record: DataType) => {
    deleteRequest.caller(record.key).then(() => {
      setRefresh(Math.random);
    });
  };

  const stopRequest = useRequest(stopInstance, {
    wrapperFun: (res: any) => {
      message.success(t('instance.message.stopSuccess'));
    },
  });

  const handleStop = (record: DataType) => {
    stopRequest.caller(record.key).then(() => {
      setRefresh(Math.random);
    });
  };

  const formatData = (res: any) => {
    setPageSize(res.size);
    setTotalCount(res.total);
    const records = res.records;
    const instance: DataType[] = [];
    Object.keys(records).forEach((i) => {
      let took = 0;
      if (records[i].startTime) {
        if (records[i].endTime) {
          took = dayjs(records[i].endTime).diff(dayjs(records[i].startTime), 'second');
        } else if (!records[i].endTime && records[i].status == 2) {
          took = dayjs().diff(dayjs(records[i].startTime), 'second');
        }
      }
      instance.push({
        key: records[i].id,
        jobName: records[i].jobName,
        readCount: records[i].readCount,
        writeCount: records[i].writeCount,
        readQps: parseFloat(records[i].readQps).toFixed(2),
        writeQps: parseFloat(records[i].writeQps).toFixed(2),
        readBytes: records[i].readBytes,
        writeBytes: records[i].writeBytes,
        status: records[i].status,
        logContent: records[i].logContent,
        startTime: records[i].startTime,
        endTime: records[i].endTime,
        duration: took == 0 ? '' : took
      });
    });
    setTabData(instance);
    return instance;
  };
  const reqInstanceList = useRequest(getInstanceList, {
    wrapperFun: formatData,
  });

  useEffect(() => {
    console.log("jobType:", jobType);
    reqInstanceList.caller({ size: pageSize, jobType: jobType });
  }, [refresh]);

  const items: TabsProps["items"] = [
    {
      key: "tabInstance",
      label: jobType == JOB_TYPE_BATCH ? t('menu.batchInstance') : jobType == JOB_TYPE_STREAM ? t('menu.streamInstance') : t('menu.fileSyncInstance')
    },
  ];
  const statusOptions = [
    { label: t('instance.status.submitted'), value: 1 },
    { label: t('instance.status.running'), value: 2 },
    { label: t('instance.status.success'), value: 3 },
    { label: t('instance.status.failure'), value: 4 },
    { label: t('instance.status.canceled'), value: 5 },
  ];

  const onPageChange = (current: number, size: number) => {
    setPageSize(size);
    reqInstanceList.caller({ current: current, size: size, jobType: jobType });
  };

  const onFinish = (values: any) => {
    const filterdValues = Object.keys(values).reduce((acc, key) => {
      if (values[key] !== undefined && values[key] !== '') {
        acc[key] = values[key];
      }
      return acc;
    }, {} as any);
    form.getFieldValue("startTime") && (filterdValues.startTime = dayjs(form.getFieldValue("startTime")).format("YYYY-MM-DD HH:mm:ss"));
    form.getFieldValue("endTime") && (filterdValues.endTime = dayjs(form.getFieldValue("endTime")).format("YYYY-MM-DD HH:mm:ss"));
    reqInstanceList.caller({ ...filterdValues, size: pageSize, jobType: jobType });
  };

  const highlightLogs = (logs: string) => {
    const highlightedCode = hljs.highlight(logs, { language: "java" }).value;
    return highlightedCode;
  };

  return (
    <Spin spinning={reqInstanceList.loading}>
      <div className="module">
        <Form
          form={form}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 18 }}
          onFinish={onFinish}>
          <Row gutter={[16, 2]}>
            <Col span={6}>
              <Form.Item label={t('instance.search.instanceId')} name="id" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('instance.search.jobName')} name="jobName" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('instance.search.status')} name="status" style={{ marginBottom: 5, textAlign: 'left' }}>
                <Select options={statusOptions} placeholder={t('instance.search.status')} allowClear />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={[16, 2]}>
            <Col span={6}>
              <Form.Item label={t('instance.search.startDate')} name="startTime" style={{ marginBottom: 5 }}>
                <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('instance.search.endDate')} name="endTime" style={{ marginBottom: 5 }}>
                <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={[16, 2]}>
            <Col span={16}></Col>
            <Col span={2} style={{ textAlign: 'right' }}>
              <Button type="primary" htmlType="submit">{t('instance.search.search')}</Button>
            </Col>
          </Row>
        </Form>
      </div>

      <div className="module">
        <Tabs defaultActiveKey="tabInstance" items={items} />      
        <CommonTable 
          columns={columns} 
          dataSource={tabData} 
          pageSize={pageSize} 
          total={totalCount} 
          onPageChange={onPageChange}
        />
        <Modal title={t('instance.modal.logTitle')} open={isModalOpen}
          onCancel={() => setIsModalOpen(false)}
          onOk={() => setIsModalOpen(false)}
          cancelButtonProps={{ style: { display: 'none' } }}
          width="80%"
          style={{ top: 100, height: '80vh' }}
          bodyStyle={{ maxHeight: '70vh', overflowY: 'auto' }}
        >
          <Spin spinning={logLoading}>
            <Card>
              <pre>
                <code
                  className="language-java code"
                  dangerouslySetInnerHTML={{ __html: highlightLogs(logContent) }}
                />
              </pre>
            </Card>
          </Spin>
        </Modal>
      </div>
    </Spin>
  );
};

const BatchInstance = memo(S);

export default BatchInstance;