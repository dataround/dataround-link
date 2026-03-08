/**
 * @author: yuehan124@gmail.com
 * @since: 2026/02/21
 * User Role Management Component
 **/
import { EditOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
  Form,
  Modal,
  Select,
  Space,
  Spin,
  Table,
  TableProps,
  Tag,
  message
} from "antd";
import { memo, useState, useEffect } from "react";
import {
  getAllRoles,
  getUserRoleList,
  assignUserRoles,
} from "../../api/permission";
import useRequest from "../../hooks/useRequest";
import { t } from "i18next";

interface UserRoleDataType {
  key: string;
  userId: string;
  userName: string;
  email: string;
  roles: { id: string; name: string }[];
}

interface UserRoleProps {
  visible: boolean;
}

const UserRoleManagement: React.FC<UserRoleProps> = ({ visible }) => {
  const [form] = Form.useForm();
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [data, setData] = useState<UserRoleDataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  const [currentUserId, setCurrentUserId] = useState<string>('');
  const [roleOptions, setRoleOptions] = useState<any[]>([]);
  const [selectedRoleIds, setSelectedRoleIds] = useState<string[]>([]);

  const columns: TableProps<UserRoleDataType>["columns"] = [
    {
      title: t('permission.userName'),
      dataIndex: "userName",
      key: "userName",
    },
    {
      title: t('permission.email'),
      dataIndex: "email",
      key: "email",
    },
    {
      title: t('permission.roles'),
      key: "roles",
      render: (_, record) => (
        <Space size="small" wrap>
          {record.roles?.map((role) => (
            <Tag color="blue" key={role.id}>{role.name}</Tag>
          ))}
        </Space>
      ),
    },
    {
      title: t('permission.action'),
      key: "action",
      render: (_, record) => (
        <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}><EditOutlined />{t('permission.assignRole')}</Button>
      ),
    },
  ];

  const formatData = (res: any) => {
    setPageSize(res.size);
    setTotalCount(res.total);
    const tableData: UserRoleDataType[] = res.records.map((item: any) => ({
      key: item.userId,
      userId: item.userId,
      userName: item.userName,
      email: item.email,
      roles: item.roles || [],
    }));
    setData(tableData);
    return tableData;
  };

  const listRequest = useRequest(getUserRoleList, {
    wrapperFun: formatData,
  });

  const allRolesRequest = useRequest(getAllRoles, {
    wrapperFun: (resData: any) => {
      const options = resData.map((r: any) => ({ label: r.name, value: String(r.id) }));
      setRoleOptions(options);
      return options;
    },
  });

  useEffect(() => {
    if (visible) {
      listRequest.caller({ size: pageSize });
      allRolesRequest.caller();
    }
  }, [visible, refresh]);

  const handleEdit = (record: UserRoleDataType) => {
    setCurrentUserId(record.userId);
    setSelectedRoleIds(record.roles?.map((r) => String(r.id)) || []);
    setIsModalOpen(true);
  };

  const handleSaveRoles = async () => {
    try {
      await assignUserRoles(currentUserId, selectedRoleIds);
      message.success(t('permission.assignSuccess'));
      setIsModalOpen(false);
      setRefresh(Math.random);
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

      {/* UserRole Edit Modal */}
      <Modal title={t('permission.assignRole')} open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={handleSaveRoles}
        cancelText={t('common.cancel')}
        okText={t('common.confirm')}
      >
        <Card>
          <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }}>
            <Form.Item label={t('permission.selectRole')}>
              <Select
                mode="multiple"
                options={roleOptions}
                value={selectedRoleIds}
                onChange={(values) => setSelectedRoleIds(values)}
                placeholder={t('permission.selectRolePlaceholder')}
                style={{ width: '100%' }}
              />
            </Form.Item>
          </Form>
        </Card>
      </Modal>
    </>
  );
};

export default memo(UserRoleManagement);
