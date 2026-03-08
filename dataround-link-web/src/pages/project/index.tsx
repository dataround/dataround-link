/**
 * @author: yuehan124@gmail.com
 * @since: 2025-09-22
 **/
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
  Col,
  Form,
  Input,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Spin,
  Table,
  TableProps,
  Tabs,
  TabsProps,
  message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { deleteProject, getProjects, getUserList, saveOrUpdateProject } from "../../api/user";
import useRequest from "../../hooks/useRequest";
import { t } from "i18next";

interface IProps { }

interface DataType {
  key: string;
  name: string;
  description: string;
  adminIds: string[];
  memberIds: string[];
  adminNames: string[];
  createUser: string;
  createTime: string;
}
let projects: any = null;
const S: FC<IProps> = () => {
  const [form] = Form.useForm();
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState<string>(t('project.newProject'));
  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [tabData, setTabData] = useState<DataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);  

  const columns: TableProps<DataType>["columns"] = [
    {
      title: t('project.id'),
      dataIndex: "key",
      key: "key",
    },
    {
      title: t('project.name'),
      dataIndex: "name",
      key: "name",
    },
    {
      title: t('project.admin'),
      key: "adminNames",
      render: (_, record) => (
        record.adminNames.join(', ')
      )
    },
    {
      title: t('project.description'),
      dataIndex: "description",
      key: "description",
    },
    {
      title: t('project.creator'),
      dataIndex: "createUser",
      key: "createUser",
    },
    {
      title: t('project.creationTime'),
      key: "createTime",
      dataIndex: "createTime",
    },
    {
      title: t('project.action'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}><EditOutlined />{t('common.edit')}</Button>
          <Popconfirm title={t('common.confirmDelete')} onConfirm={() => handleDelete(record)}>
            <Button type="link" style={{ padding: 0, gap: '4px' }}><DeleteOutlined />{t('common.delete')}</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const formatData = (res: any) => {
    setPageSize(res.size);
    setTotalCount(res.total);
    projects = res.records;
    const tabData: DataType[] = [];
    Object.keys(projects).forEach((i) => {
      const arr1: string[] = [];
      const arr3: string[] = [];
      projects[i].admins.forEach((u: any) => {
        arr1.push(u.userId);
        arr3.push(u.userName);
      });
      const arr2: string[] = [];
      projects[i].members.forEach((u: any) => {
        arr2.push(u.userId);
      });
      tabData.push({
        key: projects[i].id,
        name: projects[i].name,
        description: projects[i].description,
        adminIds: arr1,
        memberIds: arr2,
        adminNames: arr3, // for frontend display
        createUser: projects[i].createUser,
        createTime: projects[i].createTime,
      });
    });
    setTabData(tabData);
    return tabData;
  };

  const listRequest = useRequest(getProjects, {
    wrapperFun: formatData,
  });

  useEffect(() => {
    listRequest.caller({ size: pageSize });
    reqUsers.caller({ size: 100 });
  }, [refresh]);

  const items: TabsProps["items"] = [
    {
      key: "key1",
      label: t('project.list.title')
    },
  ];

  const newProject = () => {
    setModalTitle(t('project.newProject'));
    initialValues.name = '';
    initialValues.description = '';
    initialValues.adminIds = [];
    initialValues.memberIds = [];
    form.setFieldsValue(initialValues);
    setIsModalOpen(true);
  };

  const reqSave = useRequest(saveOrUpdateProject, {
    wrapperFun: (data: any) => {
      message.success(t('project.saveSuccess'));
      setIsModalOpen(false);
      setRefresh(Math.random);
    },
  });

  const handleEdit = (record: DataType) => {
    setModalTitle(t('project.editProject'));
    initialValues.id = record.key;
    initialValues.name = record.name;
    initialValues.description = record.description;
    initialValues.adminIds = record.adminIds;
    initialValues.memberIds = record.memberIds;
    form.setFieldsValue(initialValues);
    setIsModalOpen(true);
  };
  const handleDelete = (record: DataType) => {
    deleteRequest.caller(record.key).then(() => {
      setRefresh(Math.random);
    });
  };

  const deleteRequest = useRequest(deleteProject, {
    wrapperFun: (res: any) => {
      message.success(t('project.deleteSuccess'));
    },
  });

  const onFinish = () => {
    form.validateFields().then((values) => {
      const memberIds: string[] = values.memberIds;
      const adminIds: string[] = values.adminIds;
      const members: any[] = [];
      const project = projects?.find((u: any) => u.id === values.id)
      memberIds.map((id: string) => {
        const m = project?.members.find((u: any) => u.userId === id);
        members.push(m ? m : { "userId": id });
      });
      const admins: any[] = [];
      adminIds.map((id: string) => {
        const m = project?.admins.find((u: any) => u.userId === id);
        admins.push(m ? m : { "userId": id, "isAdmin": true });
      });
      const params = { ...values, "members": members, "admins": admins };
      reqSave.caller(params);
    });
  };

  const reqUsers = useRequest(getUserList, {
    wrapperFun: (res: any) => {
      const arr: object[] = [];
      res.records.forEach((user: any) => {
        arr.push({ label: user.name, value: user.id });
      });
      setUserOptions(arr);
      return arr;
    },
  });

  const initialValues = {
    id: '',
    name: '',
    description: '',
    adminIds: [] as string[],
    memberIds: [] as string[],
  };

  const onPageChange = (current: number, size: number) => {
    setPageSize(size);
    listRequest.caller({ current: current, size: size });
  };


  return (
    <Spin spinning={listRequest.loading}>
      <div className="module">
        <Form
          form={form}
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 16 }}
          onFinish={onFinish}>
          <Row gutter={[16, 2]}>
            <Col span={6}>
              <Form.Item label={t('project.id')} name="id" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('project.name')} name="name" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
          </Row>           
          <Row gutter={[16, 2]}>
            <Col span={16}></Col>
            <Col span={2} style={{ textAlign: 'right' }}>
              <Button type="primary" htmlType="submit">{t('common.search')}</Button>
            </Col>
          </Row>
        </Form>
      </div>
      <div className="module">
        <Tabs defaultActiveKey="1" items={items} tabBarExtraContent={
          <Button type="primary" htmlType="submit" onClick={newProject}>{t('project.newProject')}</Button>
        }
        />
        <Table size="small" columns={columns} dataSource={tabData} pagination={{ pageSize: pageSize, total: totalCount, onChange: onPageChange }} />
        <Modal title={modalTitle} open={isModalOpen}
          onCancel={() => setIsModalOpen(false)}
          onOk={onFinish}
          cancelText={t('common.cancel')}
          okText={t('common.confirm')}
          width="45%"
          style={{ top: 100, height: '80vh' }}
          bodyStyle={{ maxHeight: '70vh', overflowY: 'auto' }}
        >
          <Card>
            <Form
              form={form}
              labelCol={{ span: 6 }}
              wrapperCol={{ span: 18 }}
              initialValues={initialValues}
              onFinish={onFinish}>
              <Form.Item label="id" name="id" style={{ display: 'none' }}>
                <Input />
              </Form.Item>
              <Form.Item label={t('project.name')} name="name" rules={[{ required: true, message: t('project.namePlaceholder') }]}>
                <Input placeholder={t('project.namePlaceholder')} />
              </Form.Item>
              <Form.Item label={t('project.description')} name="description">
                <Input.TextArea placeholder={t('project.descriptionPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('project.admin')} name="adminIds" rules={[{ required: true, message: t('project.adminPlaceholder') }]}>
                <Select mode="multiple" options={userOptions}></Select>
              </Form.Item>
              <Form.Item label={t('project.members')} name="memberIds" rules={[{ required: true, message: t('project.membersPlaceholder') }]}>
                <Select mode="multiple" options={userOptions}></Select>
              </Form.Item>
            </Form>
          </Card>
        </Modal>
      </div>
    </Spin>
  );
};

const User = memo(S);

export default User;
