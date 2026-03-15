/**
 * @author: yuehan124@gmail.com
 * @since: 2025-09-22
 **/
import { CrownOutlined, DeleteOutlined, EditOutlined, PlusOutlined } from "@ant-design/icons";
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
  Switch,
  Table,
  TableProps,
  Tabs,
  TabsProps,
  Tag,
  message
} from "antd";
import { FC, memo, useEffect, useState } from "react";
import { deleteProject, getProjects, getUserList, saveOrUpdateProject, getProjectMembers, saveProjectMember, deleteProjectMember } from "../../api/user";
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

interface MemberDataType {
  key: string;
  userId: string;
  userName: string;
  isAdmin: boolean;
  projectId: string;
}

let projects: any = null;
const S: FC<IProps> = () => {
  const [form] = Form.useForm();
  const [memberForm] = Form.useForm();
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isMemberModalOpen, setIsMemberModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState<string>(t('project.newProject'));
  const [memberModalTitle, setMemberModalTitle] = useState<string>(t('projectMember.newMember'));
  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [tabData, setTabData] = useState<DataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  const [activeTab, setActiveTab] = useState<string>('projects');
  const [projectOptions, setProjectOptions] = useState<any[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<string>('');
  const [memberData, setMemberData] = useState<MemberDataType[]>([]);
  const [editingMember, setEditingMember] = useState<MemberDataType | null>(null);

  // Project columns
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

  // Member columns
  const memberColumns: TableProps<MemberDataType>["columns"] = [
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
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEditMember(record)}><EditOutlined />{t('common.edit')}</Button>
          <Popconfirm title={t('common.confirmDelete')} onConfirm={() => handleDeleteMember(record)}>
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
    const projOptions: any[] = [];
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
        adminNames: arr3,
        createUser: projects[i].createUser,
        createTime: projects[i].createTime,
      });
      projOptions.push({ label: projects[i].name, value: projects[i].id });
    });
    setTabData(tabData);
    setProjectOptions(projOptions);
    return tabData;
  };

  const listRequest = useRequest(getProjects, {
    wrapperFun: formatData,
  });

  const formatMemberData = (res: any) => {
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
    setMemberData(data);
    return data;
  };

  const memberListRequest = useRequest(getProjectMembers, {
    wrapperFun: formatMemberData,
  });

  useEffect(() => {
    listRequest.caller({ size: pageSize });
    reqUsers.caller({ size: 100 });
  }, [refresh]);

  const handleProjectChange = (value: string) => {
    setSelectedProjectId(value);
    if (value) {
      memberListRequest.caller(value);
    } else {
      setMemberData([]);
    }
  };

  const tabItems: TabsProps["items"] = [
    {
      key: "projects",
      label: t('project.list.title')
    },
    {
      key: "members",
      label: t('menu.projectMember')
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

  const newMember = () => {
    if (!selectedProjectId) {
      message.warning(t('projectMember.selectProjectFirst'));
      return;
    }
    setMemberModalTitle(t('projectMember.newMember'));
    setEditingMember(null);
    memberForm.setFieldsValue({
      id: '',
      projectId: selectedProjectId,
      userId: undefined,
      isAdmin: false
    });
    setIsMemberModalOpen(true);
  };

  const reqSave = useRequest(saveOrUpdateProject, {
    wrapperFun: (data: any) => {
      message.success(t('project.saveSuccess'));
      setIsModalOpen(false);
      setRefresh(Math.random);
    },
  });

  const saveMemberRequest = useRequest(saveProjectMember, {
    wrapperFun: (data: any) => {
      message.success(t('projectMember.saveSuccess'));
      setIsMemberModalOpen(false);
      if (selectedProjectId) {
        memberListRequest.caller(selectedProjectId);
      }
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

  const handleEditMember = (record: MemberDataType) => {
    setMemberModalTitle(t('projectMember.editMember'));
    setEditingMember(record);
    memberForm.setFieldsValue({
      id: record.key,
      projectId: record.projectId,
      userId: record.userId,
      isAdmin: record.isAdmin
    });
    setIsMemberModalOpen(true);
  };

  const handleDelete = (record: DataType) => {
    deleteRequest.caller(record.key).then(() => {
      setRefresh(Math.random);
    });
  };

  const handleDeleteMember = (record: MemberDataType) => {
    deleteMemberRequest.caller(record.key);
  };

  const deleteRequest = useRequest(deleteProject, {
    wrapperFun: (res: any) => {
      message.success(t('project.deleteSuccess'));
    },
  });

  const deleteMemberRequest = useRequest(deleteProjectMember, {
    wrapperFun: (res: any) => {
      message.success(t('projectMember.deleteSuccess'));
      if (selectedProjectId) {
        memberListRequest.caller(selectedProjectId);
      }
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

  const onMemberFinish = () => {
    memberForm.validateFields().then((values) => {
      const params = {
        id: values.id || null,
        projectId: values.projectId,
        userId: values.userId,
        isAdmin: values.isAdmin || false
      };
      saveMemberRequest.caller(params);
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

  const renderProjectTab = () => (
    <div className="module">
      <Table size="small" columns={columns} dataSource={tabData} pagination={{ pageSize: pageSize, total: totalCount, onChange: onPageChange }} />
    </div>
  );

  const renderMemberTab = () => (
    <div className="module">
      <Table
        size="small"
        columns={memberColumns}
        dataSource={memberData}
        pagination={{ pageSize: 10, showTotal: (total) => `Total ${total} items` }}
        locale={{ emptyText: t('projectMember.noData') }}
      />
    </div>
  );

  return (
    <Spin spinning={listRequest.loading || memberListRequest.loading}>
      {/* Search Form - Independent Block */}
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
            {activeTab === 'members' && (
              <Col span={6}>
                <Form.Item label={t('project.name')} style={{ marginBottom: 5 }}>
                  <Select
                    options={projectOptions}
                    onChange={handleProjectChange}
                    placeholder={t('projectMember.selectProject')}
                    allowClear
                  />
                </Form.Item>
              </Col>
            )}
          </Row>
          <Row gutter={[16, 2]}>
            <Col span={16}></Col>
            <Col span={2} style={{ textAlign: 'right' }}>
              <Button type="primary" htmlType="submit">{t('common.search')}</Button>
            </Col>
          </Row>
        </Form>
      </div>
      {/* Tabs Section */}
      <div className="module">
        <Tabs 
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabItems} 
          tabBarExtraContent={
            activeTab === 'projects' 
              ? <Button type="primary" onClick={newProject}>{t('project.newProject')}</Button>
              : <Button type="primary" onClick={newMember}><PlusOutlined />{t('projectMember.newMember')}</Button>
          }
        />
        {activeTab === 'projects' ? renderProjectTab() : renderMemberTab()}
      </div>
      {/* Project Modal */}
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
      {/* Member Modal */}
      <Modal
        title={memberModalTitle}
        open={isMemberModalOpen}
        onCancel={() => setIsMemberModalOpen(false)}
        onOk={onMemberFinish}
        cancelText={t('common.cancel')}
        okText={t('common.confirm')}
      >
        <Form
          form={memberForm}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 18 }}
          onFinish={onMemberFinish}
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
    </Spin>
  );
};

const Project = memo(S);

export default Project;