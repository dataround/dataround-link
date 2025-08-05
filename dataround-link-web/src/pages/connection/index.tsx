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
  Form, Popconfirm,
  Popover,
  Space,
  Table,
  TableProps,
  Tabs, message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { deleteConnection, getConnectionById, getConnections } from "../../api/connection";
import useRequest from "../../hooks/useRequest";
import { useNavigate } from "react-router-dom";
import TabPane from "antd/es/tabs/TabPane";
import { DeleteOutlined, EditOutlined, EyeOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { connectionStore } from "../../store";
import { useTranslation } from "react-i18next";

interface IProps { }

interface DataType {
  key: string;
  name: string;
  connector: string;
  host: string;
  port: number;
  user: string;
  passwd: string;
  config: string;
  description: string;
  createUser: string;
  createTime: string;
  updateTime: string;
}

const S: FC<IProps> = () => {
  const { t } = useTranslation();
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [activeKey, setActiveKey] = useState<string>('Database');
  const [refresh, setRefresh] = useState<number>(0);
  const [tabData, setTabData] = useState<DataType[]>([]);

  const columnHead: TableProps<DataType>["columns"] = [
    {
      title: t('connection.table.name'),
      dataIndex: "name",
      key: "name"
    },
    {
      title: t('connection.table.connector'),
      dataIndex: "connector",
      key: "connector",
    }
  ];

  const columnTail: TableProps<DataType>["columns"] = [
    {
      title: t('connection.table.otherParams'),
      dataIndex: "config",
      key: "config",
      render: (text) => <><Popover content={typeof text === 'object' ? JSON.stringify(text, null, 2) : text}>
        <Button type="link"><InfoCircleOutlined /></Button>
      </Popover></>,
    },
    {
      title: t('connection.table.description'),
      dataIndex: "description",
      key: "description",
    },
    {
      title: t('connection.table.createUser'),
      dataIndex: "createUser",
      key: "createUser",
    },
    {
      title: t('connection.table.createTime'),
      key: "createTime",
      dataIndex: "createTime",
    },
    {
      title: t('connection.table.operation'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}><EditOutlined />{t('connection.table.edit')}</Button>
          <Popconfirm title={t('connection.modal.deleteConfirm')} onConfirm={() => handleDelete(record)}>
            <Button type="link" style={{ padding: 0, gap: '4px' }}><DeleteOutlined />{t('connection.table.delete')}</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const columnJDBC: TableProps<DataType>["columns"] = [...columnHead,
  {
    title: t('connection.table.host'),
    dataIndex: "host",
    key: "host"
  },
  {
    title: t('connection.table.port'),
    dataIndex: "port",
    key: "port"
  },
  {
    title: t('connection.table.username'),
    dataIndex: "user",
    key: "user"
  },
  {
    title: t('connection.table.password'),
    dataIndex: "passwd",
    key: "passwd",
    render: (text) => <Popover content={text} trigger="click"><Button type="link"><EyeOutlined /></Button></Popover>,
  }, ...columnTail
  ]
  const columnsCdc: TableProps<DataType>["columns"] = [...columnHead, ...columnTail];
  const columnsNonstructural: TableProps<DataType>["columns"] = [...columnJDBC];

  const formatData = (res: any) => {
    const conns: DataType[] = [];
    Object.keys(res).forEach((i) => {
      conns.push({
        key: res[i].id,
        name: res[i].name,
        host: res[i].host,
        port: res[i].port,
        user: res[i].user,
        passwd: res[i].passwd,
        config: res[i].config,
        connector: res[i].connector,
        description: res[i].description,
        createUser: res[i].createUser,
        createTime: res[i].createTime,
        updateTime: res[i].updateTime
      });
    });
    setTabData(conns);
    return conns;
  };

  const reqList = useRequest(getConnections, {
    wrapperFun: formatData,
  });

  useEffect(() => {
    reqList.caller({ "type": activeKey === "Database" ? 1 : 2 });
  }, [activeKey, refresh]);

  const onTabChange = (key: string) => {
    setActiveKey(key);
  };

  const reqConnection = useRequest(getConnectionById, {
    wrapperFun: (res: any) => {
      // edit connection
      connectionStore.setValues({ ...res, type: res.type });
      navigate("/connection/create");
      return res;
    }
  });

  // handle edit
  const handleEdit = (record: DataType) => {
    reqConnection.caller(record.key);
  };

  // handle delete
  const handleDelete = (record: DataType) => {
    reqDelete.caller(record.key).then(() => {
      setRefresh(Math.random);
    });;
  };


  const reqDelete = useRequest(deleteConnection, {
    wrapperFun: (res: any) => {
      if (res === true) {
        message.success(t('connection.message.deleteSuccess'));
      } else {
        message.error(t('connection.message.deleteFailed'));
      }
    },
  });

  const newConnection = () => {
    navigate(`/connection/create`);
  };

  return (
    <>
      <Tabs activeKey={activeKey} onChange={onTabChange} tabBarExtraContent={
        <Button type="primary" htmlType="submit" onClick={newConnection}>{t('connection.new')}</Button>
      }
      >
        <TabPane tab={t('connection.tabs.database')} key="Database">
          <Table size="small" columns={columnJDBC} dataSource={tabData} />
        </TabPane>
        {/* <TabPane tab={t('connection.tabs.cdc')} key="CDC">
          <Table size="small" columns={columnsCdc} dataSource={tabData} />
        </TabPane> */}
        <TabPane tab={t('connection.tabs.nonstructural')} key="File">
          <Table size="small" columns={columnsNonstructural} dataSource={tabData} />
        </TabPane>
      </Tabs>

    </>
  );
};

const Connection = memo(S);

export default Connection;