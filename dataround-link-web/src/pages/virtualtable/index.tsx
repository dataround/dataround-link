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
  Form,
  Popconfirm,
  Popover,
  Space,
  Table,
  TableProps,
  Tabs,
  TabsProps,
  message,
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { deleteVirtualTable, getVirtualTableById, getVirtualTableList } from "../../api/virtualtable";
import useRequest from "../../hooks/useRequest";
import { useNavigate } from "react-router-dom";
import { DeleteOutlined, EditOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { vtableStore } from "../../store";
import { useTranslation } from "react-i18next";

interface IProps { }

interface DataType {
  key: string;
  tableName: string;
  database: string;
  connectionName: string;
  fields: string;
  description: string;
  createUser: string;
  createTime: string
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [tabData, setTabData] = useState<DataType[]>([]);
  const [refresh, setRefresh] = useState<number>(0);

  const formatData = (res: any) => {
    const vtable: DataType[] = [];
    Object.keys(res).forEach((i) => {
      vtable.push({
        key: res[i].id,
        tableName: res[i].tableName,
        database: res[i].database,
        connectionName: res[i].connectionName,
        fields: res[i].fields,
        description: res[i].description,
        createUser: res[i].createUser,
        createTime: res[i].createTime
      });
    });
    setTabData(vtable);
    return vtable;
  };
  const list = useRequest(getVirtualTableList, {
    wrapperFun: formatData,
  });

  useEffect(() => {
    list.caller();
  }, [refresh]);

  const columns: TableProps<DataType>["columns"] = [
    {
      title: t('virtualTable.table.id'),
      dataIndex: "key",
      key: "key"
    },
    {
      title: t('virtualTable.table.tableName'),
      dataIndex: "tableName",
      key: "tableName"
    },
    {
      title: t('virtualTable.table.database'),
      dataIndex: "database",
      key: "database",
    },
    {
      title: t('virtualTable.table.connectionName'),
      dataIndex: "connectionName",
      key: "connectionName",
    },
    {
      title: t('virtualTable.table.fields'),
      dataIndex: "fields",
      key: "fields",
      render: (text) => <><Popover content={text}>
        <Button type="link"><InfoCircleOutlined /></Button>
      </Popover></>,
    },
    {
      title: t('virtualTable.table.description'),
      dataIndex: "description",
      key: "description",
    },
    {
      title: t('virtualTable.table.createUser'),
      dataIndex: "createUser",
      key: "createUser",
    },
    {
      title: t('virtualTable.table.createTime'),
      key: "createTime",
      dataIndex: "createTime",
    },
    {
      title: t('virtualTable.table.operation'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}><EditOutlined />{t('virtualTable.table.edit')}</Button>
          <Popconfirm title={t('virtualTable.modal.deleteConfirm')} onConfirm={() => handleDelete(record)}>
            <Button type="link" style={{ padding: 0, gap: '4px' }}><DeleteOutlined />{t('virtualTable.table.delete')}</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const reqVTable = useRequest(getVirtualTableById, {
    wrapperFun: (res: any) => {
      console.log(res);
      vtableStore.setValues(res);
      navigate("/vtable/create");
      return res;
    }
  });

  // handle edit
  const handleEdit = (record: DataType) => {
    reqVTable.caller(record.key);
  };

  // handle delete
  const handleDelete = (record: DataType) => {
    reqDelete.caller(record.key).then(() => {
      setRefresh(Math.random);
    });;
  };

  const reqDelete = useRequest(deleteVirtualTable, {
    wrapperFun: (res: any) => {
      if (res === true) {
        message.success(t('virtualTable.message.deleteSuccess'));
      } else {
        message.error(t('virtualTable.message.deleteFailed'));
      }
    },
  });

  const items: TabsProps["items"] = [
    {
      key: "tabList",
      label: t('virtualTable.title')
    },
  ];

  const newConnection = () => {
    vtableStore.setValues({});
    navigate(`/vtable/create`);
  };

  return (
    <>
      <Tabs defaultActiveKey="tabList" items={items} tabBarExtraContent={
        <Button type="primary" htmlType="submit" onClick={newConnection}>{t('virtualTable.new')}</Button>
      }
      />
      <Table size="small" columns={columns} dataSource={tabData} />
    </>
  );
};

const VirtualTable = memo(S);

export default VirtualTable;