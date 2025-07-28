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
  Checkbox,
  Col,
  Form,
  Popconfirm,
  Radio,
  RadioChangeEvent,
  Row,
  Select,
  Space,
  Spin,
  Table,
  TableProps,
  Tabs,
  message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { getAllTableColumns, getTableColumns } from "../../../api/connection";
import useRequest from "../../../hooks/useRequest";
import { jobStore } from "../../../store";
import { FieldType, RecordType } from "./step-source";
import { useTranslation } from 'react-i18next';

interface IProps { }

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const [activeKey, setActiveKey] = useState<string>('');
  // used to select tag options
  const [sourceFields, setSourceFields] = useState<any[]>();
  // 1: insert, 2: upsert
  const [writeType, setWriteType] = useState(1);
  // 1: by name, 2: by sort
  const [matchMethod, setMatchMethod] = useState(1);
  const [tableData, setTableData] = useState<FieldType[]>([]);
  // e.g {table1: {writeType: 1, matchMethod: 1, tableData: []}, table2: {...}}
  const [fieldMapping, setFieldMapping] = useState<RecordType[]>(jobStore.tableMapping);

  const columns: TableProps<FieldType>["columns"] = [
    {
      title: t('job.edit.mapping.table.sourceFieldName'),
      dataIndex: "sourceFieldName",
      key: "sourceFieldName",
      render: (text: string, record: FieldType) => {
        return <Select size="middle" style={{ width: 150 }} options={sourceFields} defaultValue={text} onChange={(val: string) => { onFieldChanged(val, record) }}></Select>;
      }
    },
    {
      title: t('job.edit.mapping.table.sourceFieldType'),
      dataIndex: "sourceFieldType",
      key: "sourceFieldType"
    },
    {
      title: t('job.edit.mapping.table.sourcePrimaryKey'),
      dataIndex: "sourcePrimaryKey",
      key: "sourcePrimaryKey",
      render: (text: boolean) => <Checkbox checked={text} />
    },
    {
      title: t('job.edit.mapping.table.targetFieldName'),
      dataIndex: "targetFieldName",
      key: "targetFieldName",
    },
    {
      title: t('job.edit.mapping.table.targetFieldType'),
      dataIndex: "targetFieldType",
      key: "targetFieldType",
    },
    {
      title: t('job.edit.mapping.table.targetPrimaryKey'),
      dataIndex: "targetPrimaryKey",
      key: "targetPrimaryKey",
      render: (text: boolean) => <Checkbox checked={text} />
    },
    {
      title: t('job.edit.mapping.table.operation'),
      key: "action",
      render: (_, record) => (
        <Space size="middle">
          <Popconfirm title={t('job.edit.mapping.modal.deleteConfirm')} onConfirm={() => handleDelete(record.targetFieldName)}>
            <a>{t('common.delete')}</a>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // set ActiveKey
  useEffect(() => {
    if (jobStore.tableMapping.length > 0) {
      setActiveKey(jobStore.tableMapping[0].sourceTable);
    }
  }, [jobStore.tableMapping]);

  useEffect(() => {
    if (activeKey === '') {
      return;
    }
    // request source fields, used to FieldMapping table's select options
    console.log('request source table fields: ', activeKey);
    reqSourceFields.caller({ "connId": jobStore.sourceConnId, "dbName": jobStore.sourceDbName, "tableName": activeKey });

    // request all table fields, used to tableData
    let params = {}
    jobStore.tableMapping.forEach(item => {
      if (item.sourceTable == activeKey) {
        if (item.targetDbName == "") {
          message.error(t('job.edit.mapping.message.targetDbRequired'));
          return;
        }
        if (item.targetTable == "") {
          message.error(t('job.edit.mapping.message.targetTableRequired'));
          return;
        }
        params = {
          "sourceConnId": jobStore.sourceConnId, "sourceDbName": item.sourceDbName, "sourceTable": item.sourceTable,
          "targetConnId": jobStore.targetConnId, "targetDbName": item.targetDbName, "targetTable": item.targetTable, "matchMethod": matchMethod
        }
      }
    })
    // we should check using store's data or request new data
    let needRequest = true;
    jobStore.tableMapping.forEach(key => {
      if (key.sourceTable === activeKey) {
        if (key.matchMethod === matchMethod) {
          if (key.fieldData.length != 0) {
            needRequest = false;
            setWriteType(key.writeType);
            setTableData(key.fieldData);
          }
        }
      }
    });
    if (needRequest) {
      reqAllTableFields.caller(params);
    }
  }, [activeKey, matchMethod]);

  const reqSourceFields = useRequest(getTableColumns, {
    wrapperFun: (res: any) => {
      const arr: any[] = [];
      Object.keys(res).forEach((i) => {
        arr.push({
          key: res[i].name,
          label: res[i].name,
          value: res[i].name,
          type: res[i].type,
          primaryKey: res[i].primaryKey,
          nullable: res[i].nullable,
          defaultValue: res[i].defaultValue,
        })
      });
      setSourceFields(arr);
    }
  });

  const reqAllTableFields = useRequest(getAllTableColumns, {
    wrapperFun: (res: any, excessParams: any) => {
      const arr: any[] = [];
      res.forEach((item: any) => {
        item['key'] = item.targetFieldName;
        arr.push(item);
      });
      setTableData(arr);
      return arr;
    }
  });

  // handle edit
  const handleDelete = (key: string) => {
    setTableData(tableData.filter(item => item.targetFieldName !== key));
  };

  const onFieldChanged = (val: string, record: any) => {
    // change tableData
    const arr: FieldType[] = [];
    tableData.forEach((item) => {
      if (item.targetFieldName == record.targetFieldName) {
        sourceFields?.forEach((f) => {
          if (f.label == val) {
            item.sourceFieldType = f.type;
            item.sourcePrimaryKey = f.primaryKey;
            item.sourceNullable = f.nullable;
          }
        })
        arr.push(item);
      } else {
        arr.push(item);
      }
    })
    setTableData(arr);
  }

  const onTabChange = (key: string) => {
    setActiveKey(key);
  };

  const onWriteTypeChange = (e: RadioChangeEvent) => {
    setWriteType(e.target.value);
  };

  const onMatchMethodChange = (e: RadioChangeEvent) => {
    setMatchMethod(e.target.value);
  };

  useEffect(() => {
    if (activeKey !== '') {
      fieldMapping.forEach(item => {
        if (item.sourceTable === activeKey) {
          item.fieldData = tableData;
          item.writeType = writeType;
          item.matchMethod = matchMethod;
        }
      });
      jobStore.setTableMapping([...fieldMapping]);
    }
  }, [tableData, writeType, matchMethod]);

  return (
    <Spin spinning={reqAllTableFields.loading}>
      <Tabs type="line" activeKey={activeKey} onChange={onTabChange} items={jobStore.tableMapping.map(item => { return { key: item.sourceTable, label: item.sourceTable } })} />
      <Form>
        <Row gutter={[8, 0]}>
          <Col span={12}>
            <Form.Item label={t('job.edit.mapping.form.writeType')}>
              <Radio.Group onChange={onWriteTypeChange} value={writeType}>
                <Radio value={1}>{t('job.edit.mapping.writeType.insert')}</Radio>
                <Radio value={2}>{t('job.edit.mapping.writeType.upsert')}</Radio>
              </Radio.Group>
            </Form.Item>
          </Col>
          <Col span={12}>
            <div style={{ float: "right", marginRight: "20" }}>
              <Radio.Group onChange={onMatchMethodChange} value={matchMethod}>
                <Radio value={1}>{t('job.edit.mapping.matchMethod.byName')}</Radio>
                <Radio value={2}>{t('job.edit.mapping.matchMethod.byOrder')}</Radio>
              </Radio.Group>
            </div>
          </Col>
        </Row>
        <Table bordered size="small" columns={columns} dataSource={tableData} />
      </Form>
    </Spin>
  );
};

const StepMapping = memo(S);

export default StepMapping;