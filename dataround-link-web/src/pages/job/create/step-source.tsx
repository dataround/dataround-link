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
import { Checkbox, Col, Form, Input, List, Popconfirm, Row, Select, Table, Tabs } from "antd";
import { FC, memo, useEffect, useState, forwardRef, useImperativeHandle } from "react";
import useRequest from "../../../hooks/useRequest";
import { useNavigate } from "react-router-dom";
import { getConnections, getDatabaseList, getTableList } from "../../../api/connection";
import { CheckboxChangeEventTarget, CheckboxProps } from "antd/es/checkbox/Checkbox";
import { useTranslation } from 'react-i18next';
import { JobFormData, StepRef } from './index';

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
  fieldMapping: FieldType[]
}

interface IProps { 
  data: JobFormData;
  onDataChange: (updates: Partial<JobFormData>) => void;
}

const S = forwardRef<StepRef, IProps>((props, ref) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { data, onDataChange } = props;
  const jobType = data.jobType;
  const [sourceConnId, setSourceConnId] = useState<string>(data.sourceConnId || '');
  const [sourceDbName, setSourceDbName] = useState<string>(data.sourceDbName || '');
  const [targetConnId, setTargetConnId] = useState<string>(data.targetConnId || '');
  const [targetDbName, setTargetDbName] = useState<string>(data.targetDbName || '');
  const [sourceTables, setSourceTables] = useState<any[]>([]);
  const [sourceConnOptions, setSourceConnOptions] = useState<object[]>([]);
  const [sourceDbOptions, setSourceDbOptions] = useState<object[]>([]);
  const [targetTables, setTargetTables] = useState<object[]>([]);
  const [targetConnOptions, setTargetConnOptions] = useState<object[]>([]);
  const [targetDbOptions, setTargetDbOptions] = useState<object[]>([]);
  const [tableMapping, setTableMapping] = useState<RecordType[]>(data.tableMapping || []);
  const [checkedAll, setCheckedAll] = useState(false);
  const [indeterminate, setIndeterminate] = useState(false);
  const [isInitialized, setIsInitialized] = useState(false);
  // add validation errors state for targetTable
  const [validationErrors, setValidationErrors] = useState<Set<string>>(new Set());

  // expose validate and get data method
  useImperativeHandle(ref, () => ({
    validateFields: async () => {
      try {
        await form.validateFields();
        
        // validate tableMapping targetTable manually
        const invalidTables = new Set<string>();
        tableMapping.forEach(item => {
          if (!item.targetTable) {
            invalidTables.add(item.sourceTable);
          }
        });
        
        setValidationErrors(invalidTables);
        const isValid = tableMapping.length > 0 && invalidTables.size === 0;
        
        if (isValid) {
          onDataChange({ sourceConnId, sourceDbName, targetConnId, targetDbName, tableMapping: tableMapping });
        }
        return isValid;
      } catch (error) {
        return false;
      }
    },
    getFieldsValue: () => ({ sourceConnId, sourceDbName, targetConnId, targetDbName, tableMapping: tableMapping })
  }));

  const columns = [
    { key: 'sourceDbName', title: t('job.edit.source.table.sourceDb'), dataIndex: 'sourceDbName' },
    { key: 'sourceTable', title: t('job.edit.source.table.sourceTable'), dataIndex: 'sourceTable' },
    { key: 'targetDbName', title: t('job.edit.source.table.targetDb'), dataIndex: 'targetDbName' },
    {
      key: 'targetTable',
      title: <span><span style={{ color: 'red', marginRight: '4px' }}>*</span>{t('job.edit.source.table.targetTable')}</span>,
      dataIndex: 'targetTable',
      render: (text: string, record: any) => {
        const hasError = validationErrors.has(record.sourceTable);
        return (
          <Select 
            size="small" 
            style={{ 
              width: '100%', 
              height: '32px', 
              lineHeight: '32px', 
              margin: '-6px 0 -6px 0' 
            }} 
            status={hasError ? 'error' : undefined}
            showSearch 
            options={targetTables} 
            value={record.targetTable || undefined} 
            placeholder={t('job.edit.source.placeholder.selectTable')} 
            onChange={(val: string) => { 
              record.targetTable = val; 
              // clear validation error when user selects a value
              if (val && validationErrors.has(record.sourceTable)) {
                const newErrors = new Set(validationErrors);
                newErrors.delete(record.sourceTable);
                setValidationErrors(newErrors);
              }
              setTableMapping([...tableMapping]); 
            }} 
          />
        );
      }
    },
    {
      key: 'whereClause',
      title: t('job.edit.source.table.filter'),
      dataIndex: 'whereClause',
      render: (text: string, record: any) => (
        <Input size="small" style={{ width: '100%', height: '32px', lineHeight: '32px', margin: '-6px 0 -6px 0' }} 
          defaultValue={text} placeholder={t('job.edit.source.placeholder.whereClause')} 
          onChange={(e) => { record.whereClause = e.target.value; setTableMapping([...tableMapping]); }} />
      )
    },
    {
      key: 'op',
      title: t('job.edit.source.table.operation'),
      render: (_: any, record: any) => tableMapping.length >= 1 ? (
        <Popconfirm title={t('job.edit.source.modal.deleteConfirm')} onConfirm={() => handleDelete(record.sourceTable)}>
          <a>{t('common.delete')}</a>
        </Popconfirm>
      ) : null,
    },
  ];

  useEffect(() => {
    reqSourceConnection.caller({"types":["Database", "MQ"]});
    reqTargetConnection.caller({"types":["Database", "MQ"]});
  }, []);

  // only initialize once in edit mode (when data changes from empty to non-empty)
  useEffect(() => {
    if (!isInitialized) {
      // update form
      form.setFieldsValue({
        sourceConnection: data.sourceConnId,
        sourceDatabase: data.sourceDbName,
        targetConnection: data.targetConnId,
        targetDatabase: data.targetDbName,
      });
      
      // load dropdown options for edit mode
      if (data.sourceConnId) reqSourceDatabase.caller(data.sourceConnId);
      if (data.targetConnId) reqTargetDatabase.caller(data.targetConnId);
      if (data.sourceDbName && data.sourceConnId) {
        reqSourceTables.caller({ dbName: data.sourceDbName, connId: data.sourceConnId });
      }
      if (data.targetDbName && data.targetConnId) {
        reqTargetTables.caller({ dbName: data.targetDbName, connId: data.targetConnId });
      }
      
      setIsInitialized(true);
    }
  }, [isInitialized]);

  const formatConnections = (res: any) => {
    const arr: object[] = [];
    Object.keys(res).forEach((i) => {
      arr.push({ value: res[i].id, label: `${res[i].name} (${res[i].connector})` });
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

  const reqSourceTables = useRequest(getTableList, {
    wrapperFun: (res: any) => {
      const arr: ListType[] = [];
      Object.keys(res).forEach((i) => {
        let ischeck: boolean = tableMapping.find(item => item.sourceTable === res[i]) ? true : false;
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
    item.checked = target.checked;
    updateCheckAllBySourceTables();
    const row: RecordType = listItem2Row(item);
    if (target.checked) {
      let index = tableMapping.findIndex(item => item.sourceTable > row.sourceTable);
      if (index === -1) { index = tableMapping.length; }
      tableMapping.splice(index, 0, row);
    } else {
      let index = tableMapping.findIndex(item => item.sourceTable == row.sourceTable);
      if (index != -1) { tableMapping.splice(index, 1); }
    }
    setTableMapping([...tableMapping]);
  };

  const updateCheckAllBySourceTables = () => {
    let hasChecked = false;
    let allChecked = true;
    if (sourceTables.length > 0) {
      Object.values(sourceTables).forEach(item => {
        if (item.checked) { hasChecked = true; }
        allChecked = allChecked && item.checked;
      });
      setCheckedAll(allChecked);
      if (!allChecked) { setIndeterminate(hasChecked); } else { setIndeterminate(false); }
    }
  }

  const onCheckAllChange: CheckboxProps['onChange'] = (e) => {
    const isChecked: boolean = e.target.checked;
    sourceTables.forEach((item, index) => { item.checked = isChecked; });
    if (!isChecked) {
      setTableMapping([]);
    } else {
      const arr: RecordType[] = [];
      sourceTables.forEach((item, index) => { arr.push(listItem2Row(item)); });
      setTableMapping(arr);
    }
    setCheckedAll(isChecked);
  };

  const listItem2Row = (item: ListType) => {
    const row: RecordType = { sourceDbName: sourceDbName, sourceTable: item.value, targetDbName: targetDbName, targetTable: "", whereClause: "", writeType: 1, matchMethod: 1, fieldMapping: []}
    return row;
  };

  const handleDelete = (key: React.Key) => {
    setTableMapping(tableMapping.filter(item => item.sourceTable !== key));
    sourceTables.forEach(item => {
      if (item.label === key) { item.checked = false; }
    });
  };

  return (
    <>
      <Form form={form} labelCol={{ span: 3 }} wrapperCol={{ span: 10 }}>
        <Tabs defaultActiveKey="tabList" items={[{ key: "tabSource", label: t('job.edit.source.tabs.source') }]} />
        <Form.Item name="sourceConnection" label={t('job.edit.source.form.sourceConnection')} 
          rules={[{ required: true, message: t('job.edit.source.placeholder.selectConnection') }]}>
          <Select 
            placeholder={t('job.edit.source.placeholder.selectConnection')} 
            options={sourceConnOptions} 
            onChange={val => { 
              setSourceConnId(val); 
              setSourceDbOptions([]); 
              setSourceDbName(''); 
              form.setFieldsValue({ sourceConnection: val, sourceDatabase: undefined }); 
              if (val) reqSourceDatabase.caller(val); 
            }} 
            showSearch 
            filterOption={(input, option: any) => 
              (option?.label as unknown as string)?.toLowerCase().includes(input.toLowerCase())
            } 
          />
        </Form.Item>
        <Form.Item name="sourceDatabase" label={t('job.edit.source.form.sourceDatabase')} 
          rules={[{ required: true, message: t('job.edit.source.placeholder.selectDatabase') }]}>
          <Select 
            placeholder={t('job.edit.source.placeholder.selectDatabase')} 
            showSearch 
            options={sourceDbOptions} 
            onChange={val => {
              setSourceDbName(val); 
              form.setFieldsValue({ sourceDatabase: val }); 
              if (val) reqSourceTables.caller({ dbName: val, connId: sourceConnId });
            }} 
          />
        </Form.Item>

        <Tabs defaultActiveKey="tabList" items={[{ key: "tabTarget", label: t('job.edit.source.tabs.target') }]} />
        <Form.Item name="targetConnection" label={t('job.edit.source.form.targetConnection')} 
          rules={[{ required: true, message: t('job.edit.source.placeholder.selectConnection') }]}>
          <Select 
            placeholder={t('job.edit.source.placeholder.selectConnection')} 
            options={targetConnOptions} 
            onChange={val => { 
              setTargetConnId(val); 
              setTargetDbOptions([]); 
              setTargetDbName(''); 
              form.setFieldsValue({ targetConnection: val, targetDatabase: undefined }); 
              if (val) reqTargetDatabase.caller(val); 
            }} 
            showSearch 
            filterOption={(input, option: any) => 
              (option?.label as unknown as string)?.toLowerCase().includes(input.toLowerCase())
            } 
          />
        </Form.Item>
        <Form.Item name="targetDatabase" label={t('job.edit.source.form.targetDatabase')} 
          rules={[{ required: true, message: t('job.edit.source.placeholder.selectDatabase') }]}>
          <Select 
            placeholder={t('job.edit.source.placeholder.selectDatabase')} 
            showSearch 
            options={targetDbOptions} 
            onChange={val => { 
              setTargetDbName(val); 
              form.setFieldsValue({ targetDatabase: val }); 
              if (val) reqTargetTables.caller({ dbName: val, connId: targetConnId }); 
            }} 
          />
        </Form.Item>

        <Tabs defaultActiveKey="tabList" items={[{ key: "tabMapping", label: t('job.edit.source.tabs.mapping') }]} />
        <Row>
          <Col span={6}>
            <Table size="small" bordered dataSource={sourceTables} pagination={{ pageSize: 15 }} showHeader={true} columns={[
              {
                title: (<Checkbox indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkedAll}>{t('job.edit.source.list.selectAll')}</Checkbox>),
                dataIndex: 'value',
                key: 'value',
                render: (text: string, record: ListType) => (
                  <Checkbox checked={record.checked} onChange={(e) => onCheckedChange(e.target, record)}>{record.value}</Checkbox>)
              }
            ]} />
          </Col>
          <Col span={1}></Col>
          <Col span={16}>
            <Table bordered size="small" columns={columns} dataSource={tableMapping} pagination={{ pageSize: 15 }} />
          </Col>
        </Row>
      </Form>
    </>
  );
});

const StepSource = memo(S);

export default StepSource;