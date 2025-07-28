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
  Form, Input, List,
  Popconfirm,
  Row,
  Select,
  Table,
  Tabs, TreeSelect
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import useRequest from "../../../hooks/useRequest";
import { useNavigate } from "react-router-dom";
import { formatConnector, getConnections, getConnector, getDatabaseList, getTableList } from "../../../api/connection";
import { CheckboxChangeEventTarget, CheckboxProps } from "antd/es/checkbox/Checkbox";
import { JOB_TYPE_STREAM, jobStore } from "../../../store";
import { useTranslation } from 'react-i18next';


interface ListType {
  idx: number;
  label: string;
  value: string;
  checked: boolean
}
export interface FieldType {
  sourceFieldName: string;
  sourceFieldType: string;
  sourcePrimaryKey: boolean;
  sourceNullable: boolean;
  targetFieldName: string;
  targetFieldType: string;
  targetPrimaryKey: boolean;
  targetNullable: boolean;
}
export interface RecordType {
  sourceDbName: string;
  sourceTable: string;
  targetDbName: string;
  targetTable: string;
  whereClause: string;
  writeType: number;
  matchMethod: number;
  fieldData: FieldType[]
}

interface IProps { }
const S: FC<IProps> = (props) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const jobType = jobStore.jobType;
  // request from store
  const [sourceConnector, setSourceConnector] = useState<string>(jobStore.sourceConnector);
  const [sourceConnId, setSourceConnId] = useState<string>(jobStore.sourceConnId);
  const [sourceDbName, setSourceDbName] = useState<string>(jobStore.sourceDbName);
  const [targetConnector, setTargetConnector] = useState<string>(jobStore.targetConnector);
  const [targetConnId, setTargetConnId] = useState<string>(jobStore.targetConnId);
  const [targetDbName, setTargetDbName] = useState<string>(jobStore.targetDbName);
  // request new data
  const [sourceTables, setSourceTables] = useState<any[]>([]);
  const [sourceConnOptions, setSourceConnOptions] = useState<object[]>([]);
  const [sourceDbOptions, setSourceDbOptions] = useState<object[]>([]);
  const [targetTables, setTargetTables] = useState<object[]>([]);
  const [targetConnOptions, setTargetConnOptions] = useState<object[]>([]);
  const [targetDbOptions, setTargetDbOptions] = useState<object[]>([]);

  // table mapping
  const [tableData, setTableData] = useState<RecordType[]>(jobStore.tableMapping);
  // maintain a state to record whether all items are selected
  const [checkedAll, setCheckedAll] = useState(false);
  // maintain a state to record whether some items are selected
  const [indeterminate, setIndeterminate] = useState(false);

  const columns = [
    {
      key: 'sourceDbName',
      title: t('job.edit.source.table.sourceDb'),
      dataIndex: 'sourceDbName',
    },
    {
      key: 'sourceTable',
      title: t('job.edit.source.table.sourceTable'),
      dataIndex: 'sourceTable',
    },
    {
      key: 'targetDbName',
      title: t('job.edit.source.table.targetDb'),
      dataIndex: 'targetDbName',
    },
    {
      key: 'targetTable',
      title: t('job.edit.source.table.targetTable'),
      dataIndex: 'targetTable',
      render: (text: string, record: any) => {
        return <Select size="middle" style={{ width: '100%' }} options={targetTables} defaultValue={text} onChange={(val: string) => { record.targetTable = val }}></Select>;
      }
    },
    {
      key: 'whereClause',
      title: t('job.edit.source.table.filter'),
      dataIndex: 'whereClause',
      render: (text: string, record: any) => {
        return <Input size="middle" style={{ width: '100%' }} defaultValue={text} onChange={(e) => { record.whereClause = e.target.value; }}></Input>;
      }
    },
    {
      key: 'op',
      title: t('job.edit.source.table.operation'),
      render: (_: any, record: any) =>
        tableData.length >= 1 ? (
          <Popconfirm title={t('job.edit.source.modal.deleteConfirm')} onConfirm={() => handleDelete(record.sourceTable)}>
            <a>{t('common.delete')}</a>
          </Popconfirm>
        ) : null,
    },
  ];

  // request connector
  const reqSourceConnector = useRequest(getConnector, {
    wrapperFun: formatConnector
  });

  const reqTargetConnector = useRequest(getConnector, {
    wrapperFun: formatConnector
  });

  useEffect(() => {
    reqSourceConnector.caller({"type":"source", "streamSource": jobType == JOB_TYPE_STREAM});
    reqTargetConnector.caller({"type":"sink"});
  }, []);

  useEffect(() => {
    if (sourceConnector) {
      reqSourceConnection.caller({"connector":sourceConnector});
    }
  }, [sourceConnector]);

  useEffect(() => {
    if (targetConnector) {
      reqTargetConnection.caller({"connector":targetConnector});
    }
  }, [targetConnector]);

  // request connections
  const formatConnections = (res: any) => {
    const arr: object[] = [];
    Object.keys(res).forEach((i) => {
      // res[i].id is the connection id, used to get database/table list
      arr.push({ value: res[i].id, label: res[i].name });
    });
    return arr;
  };
  const reqSourceConnection = useRequest(getConnections, {
    wrapperFun: (res: any) => {
      const arr: object[] = formatConnections(res);
      setSourceConnOptions(arr);
    }
  });
  const reqTargetConnection = useRequest(getConnections, {
    wrapperFun: (res: any) => {
      const arr: object[] = formatConnections(res);
      setTargetConnOptions(arr);
    }
  });

  useEffect(() => {
    if (sourceConnId) {
      reqSourceDatabase.caller(sourceConnId);
    }
  }, [sourceConnId]);

  useEffect(() => {
    if (targetConnId) {
      reqTargetDatabase.caller(targetConnId);
    }
  }, [targetConnId]);

  // request databases
  const formatDatabases = (res: any) => {
    const arr: object[] = [];
    Object.keys(res).forEach((i) => {
      arr.push({ value: res[i], label: res[i] });
    });
    return arr;
  };
  const reqSourceDatabase = useRequest(getDatabaseList, {
    wrapperFun: (res: any) => {
      const arr: object[] = formatDatabases(res);
      setSourceDbOptions(arr);
    }
  });
  const reqTargetDatabase = useRequest(getDatabaseList, {
    wrapperFun: (res: any) => {
      const arr: object[] = formatDatabases(res);
      setTargetDbOptions(arr);
    }
  });

  // request tables if database changed
  useEffect(() => {
    if (sourceDbName) {
      reqSourceTables.caller({ dbName: sourceDbName, connId: sourceConnId });
    }
  }, [sourceDbName]);

  useEffect(() => {
    if (targetDbName) {
      reqTargetTables.caller({ dbName: targetDbName, connId: targetConnId });
    }
  }, [targetDbName]);

  // request tables
  const reqSourceTables = useRequest(getTableList, {
    wrapperFun: (res: any) => {
      const arr: ListType[] = [];
      Object.keys(res).forEach((i) => {
        // If tableData exists in store, we should init sourceTables checked status
        let ischeck: boolean = tableData.find(item => item.sourceTable === res[i]) ? true : false;
        arr.push({ idx: Number(i), label: res[i], value: res[i], checked: ischeck });
      });
      setSourceTables(arr);
      return arr;
    }
  });
  const reqTargetTables = useRequest(getTableList, {
    wrapperFun: (res: any) => {
      const arr: any[] = [];
      Object.keys(res).forEach((i) => {
        arr.push({ label: res[i], value: res[i] });
      });
      setTargetTables(arr);
      return arr;
    }
  });

  const onCheckedChange = (target: CheckboxChangeEventTarget, item: ListType) => {
    // update sourceTables
    item.checked = target.checked;
    // update checkAll status
    updateCheckAllBySourceTables();
    // update right table rows
    const row: RecordType = listItem2Row(item);
    if (target.checked) {
      let index = tableData.findIndex(item => item.sourceTable > row.sourceTable);
      if (index === -1) {
        index = tableData.length;
      }
      tableData.splice(index, 0, row);
    } else {
      let index = tableData.findIndex(item => item.sourceTable == row.sourceTable);
      if (index != -1) {
        tableData.splice(index, 1);
      }
    }
    setTableData([...tableData]);
  };

  // update checkAll status by sourceTables
  const updateCheckAllBySourceTables = () => {
    let hasChecked = false;
    let allChecked = true;
    // if sourceTables length equals zero, both indeterminate and checkedAll is false
    if (sourceTables.length > 0) {
      Object.values(sourceTables).forEach(item => {
        if (item.checked) {
          hasChecked = true;
        }
        allChecked = allChecked && item.checked;
      });
      setCheckedAll(allChecked);
      if (!allChecked) {
        setIndeterminate(hasChecked);
      } else {
        setIndeterminate(false);
      }
    }
  }

  const onCheckAllChange: CheckboxProps['onChange'] = (e) => {
    const isChecked: boolean = e.target.checked;
    sourceTables.forEach((item, index) => {
      item.checked = isChecked;
    });
    // handle table records
    if (!isChecked) {
      setTableData([]);
    } else {
      const arr: RecordType[] = [];
      sourceTables.forEach((item, index) => {
        arr.push(listItem2Row(item));
      });
      setTableData(arr);
    }
    setCheckedAll(isChecked);
  };

  const listItem2Row = (item: ListType) => {
    const row: RecordType = { sourceDbName: sourceDbName, sourceTable: item.value, targetDbName: targetDbName, targetTable: "", whereClause: "", writeType: 1, matchMethod: 1, fieldData: []}
    return row;
  };

  // delete right table row
  const handleDelete = (key: React.Key) => {
    setTableData(tableData.filter(item => item.sourceTable !== key));
    sourceTables.forEach(item => {
      if (item.label === key) {
        item.checked = false;
      }
    });
  };

  // jobstore
  useEffect(() => {
    jobStore.setSourceConnector(sourceConnector)
    jobStore.setSourceConnId(sourceConnId);
    jobStore.setSourceDbName(sourceDbName);
    jobStore.setTargetConnector(targetConnector);
    jobStore.setTargetConnId(targetConnId);
    jobStore.setTargetDbName(targetDbName);
    jobStore.setTableMapping(tableData);
  }, [sourceConnector, sourceConnId, sourceDbName, targetConnector, targetConnId, targetDbName, tableData]);

  const initialValues = {
    sourceConnector: sourceConnector,
    sourceConnection: sourceConnId,
    sourceDatabase: sourceDbName,
    targetConnector: targetConnector,
    targetConnection: targetConnId,
    targetDatabase: targetDbName,
  };
  return (
    <>
      <Form
        form={form}
        labelCol={{ span: 3 }}
        wrapperCol={{ span: 10 }}
        initialValues={initialValues}
      >
        <Tabs defaultActiveKey="tabList" items={[{ key: "tabSource", label: t('job.edit.source.tabs.source') }]} />
        <Form.Item name="sourceConnector" label={t('job.edit.source.form.sourceType')} rules={[{ required: true }]}>
          <TreeSelect
            showSearch
            style={{ width: '100%', textAlign: 'left' }}
            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
            placeholder="Please select"
            allowClear
            treeDefaultExpandAll
            onChange={val => setSourceConnector(val)}
            treeData={reqSourceConnector.data}
          />
        </Form.Item>
        <Form.Item name="sourceConnection" label={t('job.edit.source.form.sourceConnection')}>
          <Select placeholder={t('job.edit.source.placeholder.selectConnection')} options={sourceConnOptions} onChange={val => setSourceConnId(val)}></Select>
        </Form.Item>
        <Form.Item name="sourceDatabase" label={t('job.edit.source.form.sourceDatabase')}>
          <Select placeholder={t('job.edit.source.placeholder.selectDatabase')} options={sourceDbOptions} onChange={val => setSourceDbName(val)}></Select>
        </Form.Item>

        <Tabs defaultActiveKey="tabList" items={[{ key: "tabTarget", label: t('job.edit.source.tabs.target') }]} />
        <Form.Item name="targetConnector" label={t('job.edit.source.form.targetType')} rules={[{ required: true }]}>
          <TreeSelect
            showSearch
            style={{ width: '100%', textAlign: 'left' }}
            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
            placeholder="Please select"
            allowClear
            treeDefaultExpandAll
            onChange={val => setTargetConnector(val)}
            treeData={reqTargetConnector.data}
          />
        </Form.Item>
        <Form.Item name="targetConnection" label={t('job.edit.source.form.targetConnection')}>
          <Select placeholder={t('job.edit.source.placeholder.selectConnection')} options={targetConnOptions} onChange={val => setTargetConnId(val)}></Select>
        </Form.Item>
        <Form.Item name="targetDatabase" label={t('job.edit.source.form.targetDatabase')}>
          <Select placeholder={t('job.edit.source.placeholder.selectDatabase')} options={targetDbOptions} onChange={val => setTargetDbName(val)}></Select>
        </Form.Item>

        <Tabs defaultActiveKey="tabList" items={[{ key: "tabMapping", label: t('job.edit.source.tabs.mapping') }]} />
        <Row>
          <Col span={6}>
            <List size="small" bordered dataSource={sourceTables} pagination={{ pageSize: 6 }}
              header={
                <div>
                  <Checkbox indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkedAll}>
                    {t('job.edit.source.list.selectAll')}
                  </Checkbox>
                </div>
              }
              renderItem={(item: ListType) => (
                <List.Item>
                  <Checkbox checked={item.checked} onChange={(e) => onCheckedChange(e.target, item)}>{item.value}</Checkbox>
                </List.Item>
              )}
            />
          </Col>
          <Col span={1}></Col>
          <Col span={16}>
            <Table bordered size="small" columns={columns} dataSource={tableData} pagination={{ pageSize: 6 }} />
          </Col>
        </Row>
      </Form>

    </>
  );
};

const StepSource = memo(S);

export default StepSource;