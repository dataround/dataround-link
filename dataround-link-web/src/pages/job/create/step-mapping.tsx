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
  TableProps,
  Tabs,
  message
} from "antd";
import CommonTable from "../../../components/common-table";
import { FC, memo, useEffect, useState } from "react";
import { getTableColumns } from "../../../api/connection";
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
  // target fields from target table
  const [targetFields, setTargetFields] = useState<any[]>();
  // 1: insert, 2: upsert
  const [writeType, setWriteType] = useState(1);
  // 1: by name, 2: by sort
  const [matchMethod, setMatchMethod] = useState(1);
  const [tableData, setTableData] = useState<FieldType[]>([]);
  // e.g {table1: {writeType: 1, matchMethod: 1, tableData: []}, table2: {...}}
  const tableMapping = jobStore.tableMapping;

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

  // Use Promise.all to fetch both source and target table fields
  const fetchBothTableFields = async (currentTableConfig: any) => {
    try {
      const [sourceRes, targetRes] = await Promise.all([
        getTableColumns({
          "connId": jobStore.sourceConnId,
          "dbName": currentTableConfig.sourceDbName,
          "tableName": currentTableConfig.sourceTable
        }),
        getTableColumns({
          "connId": jobStore.targetConnId,
          "dbName": currentTableConfig.targetDbName,
          "tableName": currentTableConfig.targetTable
        })
      ]);

      // Process source field data
      const sourceFieldList: any[] = [];
      const sourceData = sourceRes.data || sourceRes;
      Object.keys(sourceData).forEach((i) => {
        sourceFieldList.push({
          key: sourceData[i].name,
          label: sourceData[i].name,
          value: sourceData[i].name,
          type: sourceData[i].type,
          primaryKey: sourceData[i].primaryKey,
          nullable: sourceData[i].nullable,
          defaultValue: sourceData[i].defaultValue,
        });
      });

      // Process target field data
      const targetFieldList: any[] = [];
      const targetData = targetRes.data || targetRes;
      Object.keys(targetData).forEach((i) => {
        targetFieldList.push({
          name: targetData[i].name,
          type: targetData[i].type,
          primaryKey: targetData[i].primaryKey,
          nullable: targetData[i].nullable,
          defaultValue: targetData[i].defaultValue,
        });
      });

      // Set state
      console.log("sourceFieldList:", sourceFieldList);
      console.log("targetFieldList:", targetFieldList);
      setSourceFields(sourceFieldList);
      setTargetFields(targetFieldList);

      // After both requests are completed, perform field mapping
      performFieldMapping(sourceFieldList, targetFieldList, matchMethod);

    } catch (error) {
      console.error('Error fetching table fields:', error);
      message.error(t('common.requestError'));
    }
  };

  useEffect(() => {
    if (activeKey === '') {
      // set ActiveKey - only set default on initial load
      if (tableMapping.length > 0) {
        setActiveKey(tableMapping[0].sourceTable);
      }
      return;
    }

    // Get the configuration information for the currently active table
    let currentTableConfig = tableMapping.find(item => item.sourceTable === activeKey);
    if (!currentTableConfig) {
      return;
    }
    // show current table's writeType and tableData
    setWriteType(currentTableConfig.writeType);
    setTableData(currentTableConfig.fieldData);
    // Check if data needs to be re-requested
    if (currentTableConfig.matchMethod != matchMethod || currentTableConfig.fieldData.length == 0) {
      // clear current table data, avoid showing old data
      setTableData([]);
      setSourceFields([]);
      setTargetFields([]);
      // Use Promise.all to fetch both source and target table fields
      fetchBothTableFields(currentTableConfig);
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

  const reqTargetFields = useRequest(getTableColumns, {
    wrapperFun: (res: any) => {
      const arr: any[] = [];
      Object.keys(res).forEach((i) => {
        arr.push({
          name: res[i].name,
          type: res[i].type,
          primaryKey: res[i].primaryKey,
          nullable: res[i].nullable,
          defaultValue: res[i].defaultValue,
        });
      });
      setTargetFields(arr);
    }
  });

  // Perform field matching logic (frontend implementation)
  const performFieldMapping = (sourceFieldList: any[], targetFieldList: any[], method: number) => {
    const matchByName = method === 1; // 1: match by name, 2: match by order
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
        // Match by field name
        const matchedSource = sourceFieldList.find(sourceField =>
          targetField.name.toLowerCase() === sourceField.value.toLowerCase()
        );
        if (matchedSource) {
          fieldMapping.sourceFieldName = matchedSource.value;
          fieldMapping.sourceFieldType = matchedSource.type;
          fieldMapping.sourcePrimaryKey = matchedSource.primaryKey;
          fieldMapping.sourceNullable = matchedSource.nullable;
        }
      } else {
        // Match by field order
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
    console.log("fieldMapping changed:", mappedFields);
    setTableData(mappedFields.map((field, index) => ({
      ...field,
      key: `${activeKey}_${field.targetFieldName}_${index}`
    })));
  };

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

  // sync current tab data to store
  useEffect(() => {
    if (activeKey !== '') {
      const updatedMapping = tableMapping.map(item =>
        item.sourceTable === activeKey
          ? { ...item, fieldData: tableData, writeType, matchMethod }
          : item
      );
      jobStore.setTableMapping(updatedMapping);
    }
  }, [activeKey, tableData, writeType, matchMethod]);

  return (
    <Spin spinning={reqSourceFields.loading || reqTargetFields.loading}>
      <Tabs type="line" activeKey={activeKey} onChange={onTabChange} items={tableMapping.map(item => { return { key: item.sourceTable, label: item.sourceTable } })} />
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
        <CommonTable 
          bordered 
          columns={columns} 
          dataSource={tableData}
        />
      </Form>
    </Spin>
  );
};

const StepMapping = memo(S);

export default StepMapping;