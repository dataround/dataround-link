/**
 * @author: yuehan124@gmail.com
 * @since: 2025-02-18
 **/
import { CrownOutlined, DeleteOutlined, EditOutlined, PlusOutlined } from "@ant-design/icons";
import {
  Button,
  Col,
  Form,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Spin,
  Switch,
  Table,
  TableProps,
  Tag,
  Tabs,
  TabsProps,
  message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { getProjectMembers, getProjects, getUserList, saveProjectMember, deleteProjectMember } from "../../api/user";
import useRequest from "../../hooks/useRequest";
import { t } from "i18next";

interface IProps { }

interface MemberDataType {
  key: string;
  userId: string;
  userName: string;
  isAdmin: boolean;
  projectId: string;
}

const S: FC<IProps> = () => {
  const [form] = Form.useForm();
  const [tabData, setTabData] = useState<MemberDataType[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState<string>(t('projectMember.newMember'));
  const [projectOptions, setProjectOptions] = useState<any[]>([]);
  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<string>('');
  const [editingMember, setEditingMember] = useState<MemberDataType | null>(null);

  const columns: TableProps<MemberDataType>["columns"] = [
    {
      title: t('projectMember.userName'),
      dataIndex: "userName",
      key: "userName",
      render: (text, record) => (
        <span>
          {record.isAdmin && <CrownOutlined style={{ color: '#faad14', marginRight: 8 }} />}
          {text}
        </span>
      )
    },
    {
      title: t('projectMember.role'),
      key: "isAdmin",
      render: (_, record) => (
        record.isAdmin ? (
          <Tag color="gold">{t('projectMember.admin')}</Tag>
        ) : (
          <Tag color="blue">{t('projectMember.member')}</Tag>
        )
      )
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
    const data: MemberDataType[] = [];
    if (res && Array.isArray(res)) {
      res.forEach((item: any) => {
        data.push({
          key: item.id,
          userId: item.userId,
          userName: item.userName,
          isAdmin: item.isAdmin,
          projectId: item.projectId
        });
      });
    }
    setTabData(data);
    return data;
  };

  const listRequest = useRequest(getProjectMembers, {
    wrapperFun: formatData,
  });

  const loadProjects = useRequest(getProjects, {
    wrapperFun: (res: any) => {
      const arr: object[] = [];
      if (res && res.records) {
        res.records.forEach((project: any) => {
          arr.push({ label: project.name, value: project.id });
        });
      }
      setProjectOptions(arr);
      // Auto-select first project and trigger search
      if (arr.length > 0) {
        const firstProjectId = (arr[0] as any).value;
        setSelectedProjectId(firstProjectId);
        form.setFieldValue('projectId', firstProjectId);
        listRequest.caller(firstProjectId);
      }
      return arr;
    },
  });

  const loadUsers = useRequest(getUserList, {
    wrapperFun: (res: any) => {
      const arr: object[] = [];
      if (res && res.records) {
        res.records.forEach((user: any) => {
          arr.push({ label: user.name, value: user.id });
        });
      }
      setUserOptions(arr);
      return arr;
    },
  });

  useEffect(() => {
    loadProjects.caller({ size: 100 });
    loadUsers.caller({ size: 100 });
  }, []);

  const handleProjectChange = (value: string) => {
    setSelectedProjectId(value);
    if (value) {
      listRequest.caller(value);
    } else {
      setTabData([]);
    }
  };

  const items: TabsProps["items"] = [
    {
      key: "key1",
      label: t('projectMember.listTitle')
    },
  ];

  const newMember = () => {
    if (!selectedProjectId) {
      message.warning(t('projectMember.selectProjectFirst'));
      return;
    }
    setModalTitle(t('projectMember.newMember'));
    setEditingMember(null);
    form.setFieldsValue({
      id: '',
      projectId: selectedProjectId,
      userId: undefined,
      isAdmin: false
    });
    setIsModalOpen(true);
  };

  const handleEdit = (record: MemberDataType) => {
    setModalTitle(t('projectMember.editMember'));
    setEditingMember(record);
    form.setFieldsValue({
      id: record.key,
      projectId: record.projectId,
      userId: record.userId,
      isAdmin: record.isAdmin
    });
    setIsModalOpen(true);
  };

  const handleDelete = (record: MemberDataType) => {
    deleteRequest.caller(record.key);
  };

  const saveRequest = useRequest(saveProjectMember, {
    wrapperFun: (data: any) => {
      message.success(t('projectMember.saveSuccess'));
      setIsModalOpen(false);
      if (selectedProjectId) {
        listRequest.caller(selectedProjectId);
      }
    },
  });

  const deleteRequest = useRequest(deleteProjectMember, {
    wrapperFun: (res: any) => {
      message.success(t('projectMember.deleteSuccess'));
      if (selectedProjectId) {
        listRequest.caller(selectedProjectId);
      }
    },
  });

  const onFinish = () => {
    form.validateFields().then((values) => {
      const params = {
        id: values.id || null,
        projectId: values.projectId,
        userId: values.userId,
        isAdmin: values.isAdmin || false
      };
      saveRequest.caller(params);
    });
  };

  return (
    <Spin spinning={listRequest.loading}>
      <div className="module">
        <Form
          form={form}
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 16 }}
        >
          <Row gutter={[16, 2]}>
            <Col span={6}>
              <Form.Item label={t('project.name')} name="projectId" style={{ marginBottom: 5 }}>
                <Select
                  options={projectOptions}
                  onChange={handleProjectChange}
                  placeholder={t('projectMember.selectProject')}
                  allowClear
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>
      <div className="module">
        <Tabs defaultActiveKey="1" items={items} tabBarExtraContent={
          <Button type="primary" onClick={newMember}><PlusOutlined />{t('projectMember.newMember')}</Button>
        }
        />
        <Table
          size="small"
          columns={columns}
          dataSource={tabData}
          pagination={{ pageSize: 10, showTotal: (total) => `Total ${total} items` }}
          locale={{ emptyText: t('projectMember.noData') }}
        />
        <Modal
          title={modalTitle}
          open={isModalOpen}
          onCancel={() => setIsModalOpen(false)}
          onOk={onFinish}
          cancelText={t('common.cancel')}
          okText={t('common.confirm')}
        >
          <Form
            form={form}
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 18 }}
            onFinish={onFinish}
          >
            <Form.Item name="id" style={{ display: 'none' }}>
              <input type="hidden" />
            </Form.Item>
            <Form.Item label={t('project.name')} name="projectId">
              <Select options={projectOptions} disabled />
            </Form.Item>
            <Form.Item
              label={t('projectMember.userName')}
              name="userId"
              rules={[{ required: true, message: t('projectMember.selectUser') }]}
            >
              <Select
                options={userOptions}
                placeholder={t('projectMember.selectUser')}
                disabled={editingMember !== null}
              />
            </Form.Item>
            <Form.Item
              label={t('projectMember.isAdmin')}
              name="isAdmin"
              valuePropName="checked"
            >
              <Switch checkedChildren={t('common.yes')} unCheckedChildren={t('common.no')} />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </Spin>
  );
};

const ProjectMember = memo(S);

export default ProjectMember;
