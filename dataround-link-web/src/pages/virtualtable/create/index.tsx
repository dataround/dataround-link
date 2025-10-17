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
import { MinusCircleOutlined, PlusCircleOutlined } from "@ant-design/icons";
import {
  AutoComplete,
  Button, Checkbox,
  Col, Divider, Form,
  Input,
  Radio, Row, Select, TreeSelect, message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getConnections } from "../../../api/connection";
import { saveOrUpdateVirtualTable } from "../../../api/virtualtable";
import useRequest from "../../../hooks/useRequest";
import { vtableStore } from "../../../store";
import { useTranslation } from "react-i18next";
import "./index.less";
const { Option } = Select;

const fieldTypeOptions = [
  {
    value: 'string',
    label: 'string',
  },
  {
    value: 'int',
    label: 'int',
  },
  {
    value: 'bigint',
    label: 'long',
  },
  {
    value: 'float',
    label: 'float',
  },
  {
    value: 'double',
    label: 'double',
  },
  {
    value: 'boolean',
    label: 'boolean',
  },
  {
    value: 'date',
    label: 'date',
  },
  {
    value: 'decimal(38,18)',
    label: 'decimal(38,18)',
  }
]

interface IProps {
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const values = vtableStore.getValues();
  const tableConfig = values.tableConfig ? JSON.parse(values.tableConfig) : {};
  const vtable = { ...values, ...tableConfig, "connectionType": "Kafka", "fields": values.fields || [{}], "format": values.format || "json" };
  if (!vtable.hasOwnProperty("delimiter")) {
    vtable.delimiter = ",";
  }
  const [form] = Form.useForm();
  const [selectedConnectionType, setSelectedConnectionType] = useState<string>('Kafka');
  const [format, setFormat] = useState<string>(vtable.format);
  const navigate = useNavigate();

  const onTypeChange = (newValue: string) => {
    setSelectedConnectionType(newValue);
  };

  const reqSave = useRequest(saveOrUpdateVirtualTable, {
    wrapperFun: (res: any) => {
      message.success(t('virtualTable.create.message.saveSuccess'));
      navigate("/vtable");
    }
  });

  const onFinish = (values: any) => {
    const params = { ...vtable, ...values, jsonConfig: { topic: values.topic, delimiter: values.delimiter } };
    reqSave.caller(params);
  };

  const reqConnection = useRequest(getConnections, {
    wrapperFun: (res: any) => {
      const arr: object[] = [];
      Object.keys(res).forEach((i) => {
        arr.push({ value: res[i].id, label: res[i].name });
      });
      return arr;
    }
  });

  useEffect(() => {
    reqConnection.caller({ connector: "Kafka" });
  }, []);

  const onCancel = () => {
    navigate("/vtable");
  };

  const renderDaynamic = () => {
    if (selectedConnectionType === 'Kafka') {
      return (
        <>
          <Form.Item name="topic" label={t('virtualTable.create.form.topic')} rules={[{ required: true }]}>
            <Input placeholder={t('virtualTable.create.placeholder.enterTopic')} />
          </Form.Item>
          <Form.Item name="format" label={t('virtualTable.create.form.sourceFormat')} rules={[{ required: true }]}>
            <div style={{ textAlign: "left" }}>
              <Radio.Group onChange={(e) => { setFormat(e.target.value) }} defaultValue={format}>
                <Radio value={"json"}>{t('virtualTable.create.format.json')}</Radio>
                <Radio value={"text"}>{t('virtualTable.create.format.text')}</Radio>
              </Radio.Group>
              {format === "text" && (
                <Form.Item name="delimiter" noStyle>
                  <AutoComplete style={{ width: '120px' }}
                    options={[
                      { label: ',', value: ',' },
                      { label: ';', value: ';' },
                      { label: '#', value: '#' },
                    ]}
                    placeholder={t('virtualTable.create.form.delimiter')}
                  >
                  </AutoComplete>
                </Form.Item>
              )}
            </div>
          </Form.Item>
        </>
      );
    }
  };

  return (
    <div className="module">
      <Form
        form={form}
        labelCol={{ span: 3 }}
        onFinish={onFinish}
        initialValues={vtable}
      >
        <Divider orientation="left" orientationMargin={0}>{t('virtualTable.create.basicInfo')}</Divider>
        <Form.Item name="connectionType" label={t('virtualTable.create.form.connectionType')} rules={[{ required: true }]}>
          <TreeSelect
            showSearch
            style={{ width: '100%', textAlign: 'left' }}
            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
            placeholder="Please select"
            allowClear
            treeDefaultExpandAll
            onChange={onTypeChange}
            treeData={[{ value: 'Kafka', title: 'Kafka' }]}
          />
        </Form.Item>
        <Form.Item name="connectionId" label={t('virtualTable.create.form.connectionName')} rules={[{ required: true }]}>
          <Select placeholder={t('virtualTable.create.placeholder.selectConnection')} options={reqConnection.data} style={{ textAlign: 'left' }}></Select>
        </Form.Item>
        <Form.Item name="database" label={t('virtualTable.create.form.databaseName')} rules={[{ required: true }]}>
          <Input placeholder={t('virtualTable.create.placeholder.enterDatabase')} style={{ textAlign: 'left' }} />
        </Form.Item>
        <Form.Item name="tableName" label={t('virtualTable.create.form.tableName')} rules={[{ required: true }]}>
          <Input placeholder={t('virtualTable.create.placeholder.enterTable')} style={{ textAlign: 'left' }} />
        </Form.Item>
        <Form.Item name="description" label={t('virtualTable.create.form.description')}>
          <Input.TextArea placeholder={t('virtualTable.create.placeholder.enterDescription')} />
        </Form.Item>

        {renderDaynamic()}
        <Divider orientation="left" orientationMargin={0}>{t('virtualTable.create.fieldInfo')}</Divider>
        <Row gutter={22}>          
          <Col span={4}>
            <span>{t('virtualTable.create.form.fieldName')}</span>
          </Col>
          <Col span={4}>
            <span>{t('virtualTable.create.form.fieldType')}</span>
          </Col>
          <Col span={2}>
            <span>{t('virtualTable.create.form.nullable')}</span>
          </Col>
          <Col span={2}>
            <span>{t('virtualTable.create.form.primaryKey')}</span>
          </Col>
          <Col span={4}>
            <span>{t('virtualTable.create.form.fieldDescription')}</span>
          </Col>
          <Col span={4}>
            <span>{t('virtualTable.create.form.defaultValue')}</span>
          </Col>
          <Col span={2}>
            <span>{t('virtualTable.create.form.operation')}</span>
          </Col>
        </Row>
        <Form.List name="fields">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => (
                <Row gutter={22}>
                  <Col span={1} style={{ display: 'none' }}>
                    <Form.Item {...restField} name={[name, 'id']}>
                      <Input readOnly />
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Form.Item {...restField} name={[name, 'name']} rules={[{ required: true, message: t('virtualTable.create.placeholder.enterFieldName') }]}>
                      <Input placeholder={t('virtualTable.create.placeholder.enterFieldName')} />
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Form.Item {...restField} name={[name, 'type']} rules={[{ required: true, message: t('virtualTable.create.placeholder.selectFieldType') }]}>
                      <Select placeholder={t('virtualTable.create.placeholder.selectFieldType')} options={fieldTypeOptions} style={{ maxWidth: '128px' }} />
                    </Form.Item>
                  </Col>
                  <Col span={2}>
                    <Form.Item {...restField} name={[name, 'nullable']}>
                      <Checkbox></Checkbox>
                    </Form.Item>
                  </Col>
                  <Col span={2}>
                    <Form.Item {...restField} name={[name, 'primaryKey']}>
                      <Checkbox></Checkbox>
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Form.Item {...restField} name={[name, 'comment']}>
                      <Input placeholder={t('virtualTable.create.placeholder.enterFieldDescription')} />
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Form.Item {...restField} name={[name, 'defaultValue']}>
                      <Input placeholder={t('virtualTable.create.placeholder.enterDefaultValue')} />
                    </Form.Item>
                  </Col>
                  <Col span={2}>
                    <Form.Item {...restField} name={[name, 'op']} >
                      <span>
                        <PlusCircleOutlined onClick={() => add()} />&nbsp;
                        {index > 0 && <MinusCircleOutlined onClick={() => remove(name)} />}
                      </span>
                    </Form.Item>
                  </Col>
                </Row>
              ))}
            </>
          )}
        </Form.List>

        <Row>
          <Col span={8} style={{ marginTop: 20 }}>
            <Button htmlType="button" onClick={onCancel} style={{ marginRight: 20 }}>{t('virtualTable.create.button.cancel')}</Button>
            <Button type="primary" htmlType="submit">{t('virtualTable.create.button.save')}</Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

const VirtualTableForm = memo(S);

export default VirtualTableForm;