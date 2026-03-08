/**
 * @author: yuehan124@gmail.com
 * @since: 2026/02/21
 * Role Management Component
 **/
import { DeleteOutlined, EditOutlined, SettingOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Spin,
  Table,
  TableProps,
  Tree,
  message
} from "antd";
import { memo, useState, useEffect, forwardRef, useImperativeHandle } from "react";
import {
  deleteRole,
  getAllResources,
  getRoleList,
  getRoleResources,
  saveOrUpdateRole,
  assignRoleResources,
} from "../../api/permission";
import useRequest from "../../hooks/useRequest";
import { t } from "i18next";

interface RoleDataType {
  key: string;
  name: string;
  description: string;
  createTime: string;
}

interface ResourceType {
  id: string;
  pid: string;
  name: string;
  enName: string;
  type: string;
  resKey: string;
  method: string;
  description: string;
  createTime: string;
}

interface RoleProps {
  visible: boolean;
}

export interface RoleRef {
  newRole: () => void;
}

const RoleManagement = forwardRef<RoleRef, RoleProps>(({ visible }, ref) => {
  const [form] = Form.useForm();
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isResourceModalOpen, setIsResourceModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState<string>(t('permission.newRole'));
  const [data, setData] = useState<RoleDataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  const [currentRoleId, setCurrentRoleId] = useState<string>('');
  const [allResources, setAllResources] = useState<ResourceType[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<string[]>([]);

  const columns: TableProps<RoleDataType>["columns"] = [
    {
      title: t('permission.roleName'),
      dataIndex: "name",
      key: "name",
    },
    {
      title: t('permission.roleDesc'),
      dataIndex: "description",
      key: "description",
    },
    {
      title: t('permission.createTime'),
      key: "createTime",
      dataIndex: "createTime",
    },
    {
      title: t('permission.action'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}><EditOutlined />{t('common.edit')}</Button>
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleConfigResource(record)}><SettingOutlined />{t('permission.configResource')}</Button>
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
    const tableData: RoleDataType[] = res.records.map((item: any) => ({
      key: item.id,
      name: item.name,
      description: item.description,
      createTime: item.createTime,
    }));
    setData(tableData);
    return tableData;
  };

  const listRequest = useRequest(getRoleList, {
    wrapperFun: formatData,
  });

  const allResourcesRequest = useRequest(getAllResources, {
    wrapperFun: (resData: any) => {
      setAllResources(resData);
      return resData;
    },
  });

  const roleResourcesRequest = useRequest(getRoleResources, {
    wrapperFun: (resData: any) => {
      const keys = resData.map((r: ResourceType) => String(r.id));
      setCheckedKeys(keys);
      return resData;
    },
  });

  useEffect(() => {
    if (visible) {
      listRequest.caller({ size: pageSize });
    }
  }, [visible, refresh]);

  useImperativeHandle(ref, () => ({
    newRole: () => {
      setModalTitle(t('permission.newRole'));
      form.setFieldsValue({ id: '', name: '', description: '' });
      setIsModalOpen(true);
    }
  }));

  const saveRequest = useRequest(saveOrUpdateRole, {
    wrapperFun: () => {
      message.success(t('permission.saveSuccess'));
      setIsModalOpen(false);
      setRefresh(Math.random);
    },
  });

  const handleEdit = (record: RoleDataType) => {
    setModalTitle(t('permission.editRole'));
    form.setFieldsValue({
      id: record.key,
      name: record.name,
      description: record.description,
    });
    setIsModalOpen(true);
  };

  const handleDelete = (record: RoleDataType) => {
    deleteRequest.caller(record.key);
  };

  const deleteRequest = useRequest(deleteRole, {
    wrapperFun: () => {
      message.success(t('permission.deleteSuccess'));
      setRefresh(Math.random);
    },
  });

  const onFinish = () => {
    form.validateFields().then((values) => {
      saveRequest.caller(values);
    });
  };

  const handleConfigResource = (record: RoleDataType) => {
    setCurrentRoleId(record.key);
    allResourcesRequest.caller();
    roleResourcesRequest.caller(record.key);
    setIsResourceModalOpen(true);
  };

  const buildTreeData = (resources: ResourceType[]) => {
    const map = new Map<string, any>();
    const roots: any[] = [];
    
    resources.forEach(r => {
      const typeLabel = r.type === 'ui' ? '[UI]' : '[API]';
      map.set(String(r.id), {
        key: String(r.id),
        title: `${typeLabel} ${r.name} (${r.resKey || ''})`,
        children: [],
      });
    });
    
    resources.forEach(r => {
      const node = map.get(String(r.id));
      if (String(r.pid) === '0') {
        roots.push(node);
      } else {
        const parent = map.get(String(r.pid));
        if (parent) {
          parent.children.push(node);
        }
      }
    });
    
    return roots;
  };

  const onCheck = (checked: any) => {
    setCheckedKeys(checked as string[]);
  };

  const handleSaveResources = async () => {
    try {
      await assignRoleResources(currentRoleId, checkedKeys);
      message.success(t('permission.assignSuccess'));
      setIsResourceModalOpen(false);
    } catch (error) {
      message.error(t('common.error'));
    }
  };

  const onPageChange = (current: number, size: number) => {
    setPageSize(size);
    listRequest.caller({ current: current, size: size });
  };

  return (
    <>
      <Spin spinning={listRequest.loading}>
        <div style={{ marginTop: '16px' }}>
          <Table
            size="small"
            columns={columns}
            dataSource={data}
            pagination={{ pageSize, total: totalCount, onChange: onPageChange }}
          />
        </div>
      </Spin>

      {/* Role Edit Modal */}
      <Modal title={modalTitle} open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={onFinish}
        cancelText={t('common.cancel')}
        okText={t('common.confirm')}
      >
        <Card>
          <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} onFinish={onFinish}>
            <Form.Item label="id" name="id" style={{ display: 'none' }}>
              <Input />
            </Form.Item>
            <Form.Item label={t('permission.roleName')} name="name" rules={[{ required: true, message: t('permission.roleNamePlaceholder') }]}>
              <Input placeholder={t('permission.roleNamePlaceholder')} />
            </Form.Item>
            <Form.Item label={t('permission.roleDesc')} name="description">
              <Input.TextArea placeholder={t('permission.roleDescPlaceholder')} />
            </Form.Item>
          </Form>
        </Card>
      </Modal>

      {/* Resource Config Modal */}
      <Modal title={t('permission.configResource')} open={isResourceModalOpen}
        onCancel={() => setIsResourceModalOpen(false)}
        onOk={handleSaveResources}
        cancelText={t('common.cancel')}
        okText={t('common.confirm')}
        width="40%"
      >
        <Spin spinning={allResourcesRequest.loading || roleResourcesRequest.loading}>
          <Tree
            checkable
            checkedKeys={checkedKeys}
            onCheck={onCheck}
            treeData={buildTreeData(allResources)}
            style={{ padding: '10px 0' }}
          />
        </Spin>
      </Modal>
    </>
  );
});

export default memo(RoleManagement);
