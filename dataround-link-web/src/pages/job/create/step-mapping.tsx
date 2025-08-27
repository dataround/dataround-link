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
import { Checkbox, Col, Form, Popconfirm, Radio, RadioChangeEvent, Row, Select, Space, Spin, TableProps, Tabs, message } from "antd";
import CommonTable from "../../../components/common-table";
import { FC, memo, useEffect, useState, forwardRef, useImperativeHandle } from "react";
import { getTableColumns } from "../../../api/connection";
import useRequest from "../../../hooks/useRequest";
import { useTranslation } from 'react-i18next';
import { JobFormData, StepRef } from './index';

interface FieldType {
  sourceFieldName: string;
  sourceFieldType: string;
  sourcePrimaryKey: boolean;
  sourceNullable: boolean;
  targetFieldName: string;
  targetFieldType: string;
  targetPrimaryKey: boolean;
  targetNullable: boolean;
}

interface IProps {
  data: JobFormData;
  onDataChange: (updates: Partial<JobFormData>) => void;
}

const S = forwardRef<StepRef, IProps>((props, ref) => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const { data, onDataChange } = props;
  const [activeKey, setActiveKey] = useState<string>('');
  const [sourceFields, setSourceFields] = useState<any[]>();
  const [targetFields, setTargetFields] = useState<any[]>();
  const [writeType, setWriteType] = useState(1);
  const [matchMethod, setMatchMethod] = useState(1);
  const [fieldMapping, setFieldMapping] = useState<FieldType[]>([]);
  const [tableMapping, setTableMapping] = useState(data.tableMapping || []);

  // expose validate method
  useImperativeHandle(ref, () => ({
    validateFields: async () => {
      try {
        // save current tab data first
        if (activeKey) {
          saveCurrentTabData(activeKey);
        }

        const isValid = tableMapping.length > 0 && tableMapping.every((table: any) => table.fieldData && table.fieldData.length > 0);
        // only sync data to parent component when validation passes
        if (isValid) {
          console.log("step-mapping validation passed, syncing data:", tableMapping);
          onDataChange({ tableMapping });
        }
        return isValid;
      } catch (error) {
        return false;
      }
    },
    getFieldsValue: () => ({ tableMapping })
  }));

  // save current tab data before switching
  const saveCurrentTabData = (currentKey: string) => {
    if (currentKey && tableMapping.length > 0) {
      const updatedMapping = tableMapping.map((table: any) => {
        if (table.sourceTable === currentKey) {
          return { ...table, writeType, matchMethod, fieldMapping: fieldMapping };
        }
        return table;
      });
      setTableMapping(updatedMapping);
    }
  };

  const columns: TableProps<FieldType>["columns"] = [
    {
      title: t('job.edit.mapping.table.sourceFieldName'), dataIndex: "sourceFieldName", key: "sourceFieldName",
      render: (text: string, record: FieldType) => (
        <Select size="middle" style={{ width: 150 }} options={sourceFields} defaultValue={text}
          onChange={(val: string) => { onFieldChanged(val, record) }} />)
    },
    { title: t('job.edit.mapping.table.sourceFieldType'), dataIndex: "sourceFieldType", key: "sourceFieldType" },
    {
      title: t('job.edit.mapping.table.sourcePrimaryKey'), dataIndex: "sourcePrimaryKey", key: "sourcePrimaryKey",
      render: (text: boolean) => <Checkbox checked={text} />
    },
    { title: t('job.edit.mapping.table.targetFieldName'), dataIndex: "targetFieldName", key: "targetFieldName" },
    { title: t('job.edit.mapping.table.targetFieldType'), dataIndex: "targetFieldType", key: "targetFieldType" },
    {
      title: t('job.edit.mapping.table.targetPrimaryKey'), dataIndex: "targetPrimaryKey", key: "targetPrimaryKey",
      render: (text: boolean) => <Checkbox checked={text} />
    },
    {
      title: t('job.edit.mapping.table.operation'), key: "operation",
      render: (_: any, record: FieldType) => (
        <Space size="middle"><Popconfirm title={t('job.edit.mapping.modal.deleteConfirm')}
          onConfirm={() => handleDelete(record)}><a>{t('common.delete')}</a></Popconfirm></Space>)
    }
  ];

  const fetchBothTableFields = async (currentTableConfig: any) => {
    try {
      const sourceParams = { dbName: currentTableConfig.sourceDbName, connId: data.sourceConnId, tableName: currentTableConfig.sourceTable };
      const targetParams = { dbName: currentTableConfig.targetDbName, connId: data.targetConnId, tableName: currentTableConfig.targetTable };

      const [sourceRes, targetRes] = await Promise.all([
        getTableColumns(sourceParams),
        getTableColumns(targetParams)
      ]);

      const sourceFieldList: any[] = [];
      const sourceData = sourceRes.data || sourceRes;
      Object.keys(sourceData).forEach((i) => {
        sourceFieldList.push({ key: sourceData[i].name, label: sourceData[i].name, value: sourceData[i].name, type: sourceData[i].type, primaryKey: sourceData[i].primaryKey, nullable: sourceData[i].nullable, defaultValue: sourceData[i].defaultValue });
      });

      const targetFieldList: any[] = [];
      const targetData = targetRes.data || targetRes;
      Object.keys(targetData).forEach((i) => {
        targetFieldList.push({ name: targetData[i].name, type: targetData[i].type, primaryKey: targetData[i].primaryKey, nullable: targetData[i].nullable, defaultValue: targetData[i].defaultValue });
      });

      setSourceFields(sourceFieldList);
      setTargetFields(targetFieldList);
      performFieldMapping(sourceFieldList, targetFieldList, matchMethod);
    } catch (error) {
      console.error('Error fetching table fields:', error);
      message.error(t('common.requestError'));
    }
  };

  useEffect(() => {
    if (activeKey === '') {
      if (tableMapping.length > 0) { setActiveKey(tableMapping[0].sourceTable); }
      return;
    }

    let currentTableConfig = tableMapping.find((item: any) => item.sourceTable === activeKey);
    if (!currentTableConfig) { return; }

    setWriteType(currentTableConfig.writeType);
    setMatchMethod(currentTableConfig.matchMethod || 1);
    setFieldMapping(currentTableConfig.fieldData || []);

    if ((currentTableConfig.matchMethod || 1) !== matchMethod || (currentTableConfig.fieldData || []).length === 0) {
      setFieldMapping([]);
      setSourceFields([]);
      setTargetFields([]);
      fetchBothTableFields(currentTableConfig);
    }
  }, [activeKey]);

  const performFieldMapping = (sourceFieldList: any[], targetFieldList: any[], method: number) => {
    const matchByName = method === 1;
    const mappedFields: FieldType[] = [];

    targetFieldList.forEach((targetField, index) => {
      const fieldMapping: FieldType = {
        sourceFieldName: '',
        sourceFieldType: '',
        sourcePrimaryKey: false,
        sourceNullable: true,
        targetFieldName: targetField.name,
        targetFieldType: targetField.type,
        targetPrimaryKey: targetField.primaryKey,
        targetNullable: targetField.nullable,
      };

      if (matchByName) {
        const matchedSource = sourceFieldList.find(sourceField => targetField.name.toLowerCase() === sourceField.value.toLowerCase());
        if (matchedSource) {
          fieldMapping.sourceFieldName = matchedSource.value;
          fieldMapping.sourceFieldType = matchedSource.type;
          fieldMapping.sourcePrimaryKey = matchedSource.primaryKey;
          fieldMapping.sourceNullable = matchedSource.nullable;
        }
      } else {
        if (sourceFieldList.length > index) {
          const sourceField = sourceFieldList[index];
          fieldMapping.sourceFieldName = sourceField.value;
          fieldMapping.sourceFieldType = sourceField.type;
          fieldMapping.sourcePrimaryKey = sourceField.primaryKey;
          fieldMapping.sourceNullable = sourceField.nullable;
        }
      }

      mappedFields.push(fieldMapping);
    });

    setFieldMapping(mappedFields);
  };

  const onFieldChanged = (val: string, record: FieldType) => {
    const sourceField = sourceFields?.find(field => field.value === val);
    if (sourceField) {
      // update the specific record in tableData array correctly
      const updatedTableData = fieldMapping.map(item => {
        if (item.targetFieldName === record.targetFieldName) {
          return {
            ...item,
            sourceFieldName: sourceField.value,
            sourceFieldType: sourceField.type,
            sourcePrimaryKey: sourceField.primaryKey,
            sourceNullable: sourceField.nullable
          };
        }
        return item;
      });
      setFieldMapping(updatedTableData);
    }
  };

  const onWriteTypeChange = (e: RadioChangeEvent) => {
    const newWriteType = e.target.value;
    setWriteType(newWriteType);
  };

  const onMatchMethodChange = (e: RadioChangeEvent) => {
    const newMatchMethod = e.target.value;
    setMatchMethod(newMatchMethod);
  };

  const handleDelete = (record: FieldType) => {
    const newFieldMapping = fieldMapping.filter(item => item.targetFieldName !== record.targetFieldName);
    setFieldMapping(newFieldMapping);
  };

  const handleTabChange = (newActiveKey: string) => {
    // save current tab data before switching
    if (activeKey) {
      saveCurrentTabData(activeKey);
    }
    setActiveKey(newActiveKey);
  };

  const getTabList = () => {
    return tableMapping.map((item: any) => ({
      key: item.sourceTable,
      label: item.sourceTable,
      children: (
        <Form form={form}>
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
                <Form.Item label={t('job.edit.mapping.form.matchMethod')}>
                  <Radio.Group onChange={onMatchMethodChange} value={matchMethod}>
                    <Radio value={1}>{t('job.edit.mapping.matchMethod.byName')}</Radio>
                    <Radio value={2}>{t('job.edit.mapping.matchMethod.byOrder')}</Radio>
                  </Radio.Group>
                </Form.Item>
              </div>
            </Col>
          </Row>
          <CommonTable columns={columns} dataSource={fieldMapping} size="small" />
        </Form>
      )
    }));
  };

  return (
    <>
      <Tabs activeKey={activeKey} onChange={handleTabChange} items={getTabList()} />
    </>
  );
});

const StepMapping = memo(S);

export default StepMapping;