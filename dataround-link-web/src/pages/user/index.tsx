/**
 * @author: yuehan124@gmail.com
 * @since: 2025-09-22
 **/
import { EditOutlined, StopOutlined, UserAddOutlined, LockOutlined, CheckOutlined } from "@ant-design/icons";
import {
  Button,
  Card, Col, DatePicker,
  Form,
  Input,
  Modal,
  Popconfirm, Row, Select,
  Space,
  Spin,
  Table,
  TableProps,
  Tabs,
  TabsProps,
  Tag,
  message
} from "antd";
import { t } from "i18next";
import dayjs from "dayjs";
import { FC, memo, useEffect, useState } from "react";
import { getUserList, saveOrUpdateUser } from "../../api/user";
import useRequest from "../../hooks/useRequest";

interface IProps {}

interface DataType {
  key: string;
  name: string;
  email: string;
  cellphone: string;
  passwd: string;
  department: string;
  position: string;
  gender: string;
  birthday: string;
  wechat: string;
  address: string;
  remark: string;
  createTime: string;
  updateTime: string;
  status: number;
}

const S: FC<IProps> = () => {
  const [form] = Form.useForm();
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [modalTitle, setModalTitle] = useState<string>(t('user.newUser'));
  const [tabData, setTabData] = useState<DataType[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);  


  const columns: TableProps<DataType>["columns"] = [
    {
      title: t('user.name'),
      dataIndex: "name",
      key: "name",
    },
    {
      title: t('user.email'),
      dataIndex: "email",
      key: "email",
    },
    {
      title: t('user.cellphone'),
      dataIndex: "cellphone",
      key: "cellphone",
    },
    {
      title: t('user.department'),
      dataIndex: "department",
      key: "department",
    },
    {
      title: t('user.position'),
      dataIndex: "position",
      key: "position",
    },
    {
      title: t('user.status'),
      key: "status",
      dataIndex: "status",
      render: (_, record) => {
        const statusConfig: Record<number, { color: string; text: string }> = {
          1: { color: 'green', text: t('user.statusNormal') },
          2: { color: 'red', text: t('user.statusDisabled') },
          3: { color: 'orange', text: t('user.statusLocked') },
        };
        const config = statusConfig[record.status] || { color: 'default', text: '-' };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: t('user.createTime'),
      key: "createTime",
      dataIndex: "createTime",
    },
    {
      title: t('user.action'),
      key: "action",
      render: (_, record) => (
        <Space size="small">
          <Button type="link" style={{ padding: 0, gap: '4px' }} onClick={() => handleEdit(record)}><EditOutlined />{t('user.edit')}</Button>
          <Popconfirm title={t('user.confirmStatusChange')} onConfirm={() => handleStatusChange(record)}>
            {record.status === 1 ? (
              <Button type="link" style={{ padding: 0, gap: '4px' }}><StopOutlined />{t('user.disable')}</Button>
            ) : record.status === 2 ? (
              <Button type="link" style={{ padding: 0, gap: '4px' }}><CheckOutlined />{t('user.enable')}</Button>
            ) : (
              <Button type="link" style={{ padding: 0, gap: '4px' }}><LockOutlined />{t('user.unlock')}</Button>
            )}
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const formatData = (res: any) => {
    const users: DataType[] = [];
    const records = res.records;
    Object.keys(records).forEach((i) => {
      users.push({
        key: records[i].id,
        name: records[i].name,
        email: records[i].email,
        cellphone: records[i].cellphone,
        passwd: records[i].passwd,
        department: records[i].department,
        position: records[i].position,
        gender: records[i].gender,
        birthday: records[i].birthday,
        wechat: records[i].wechat,
        address: records[i].address,
        remark: records[i].remark,
        status: records[i].status,
        createTime: records[i].createTime,
        updateTime: records[i].updateTime,
      });
    });
    setTabData(users);
    return users;
  };
  const listRequest = useRequest(getUserList, {
    wrapperFun: formatData,
  });

  useEffect(() => {
    listRequest.caller();
  }, [refresh]);

  const items: TabsProps["items"] = [
    {
      key: "key1",
      label: t('user.listTitle'),
      children: "",
    },
  ];

  const onPageChange = (current: number, size: number) => {
    setPageSize(size);
    listRequest.caller({ current: current, size: size });
  };

  const initialValues = {
    id: '',
    name: '',
    email: '',
    cellphone: '',
    passwd: '',
    gender: undefined,
    birthday: null,
    department: '',
    position: '',
    wechat: '',
    address: '',
    remark: '',
    status: 1,
  };

  const newUsers = () => {
    setModalTitle(t('user.newUser'));
    setIsEdit(false);
    form.setFieldsValue({
      id: '',
      name: '',
      email: '',
      cellphone: '',
      passwd: '',
      gender: undefined,
      birthday: null,
      status: 1,
      department: '',
      position: '',
      wechat: '',
      address: '',
      remark: '',
    });
    setIsModalOpen(true);
  };

  const reqSave = useRequest(saveOrUpdateUser, {
    wrapperFun: (data: any) => {
      message.success(t('user.saveSuccess'));
      setIsModalOpen(false);
      setRefresh(Math.random);
    },
  });
    
  const handleEdit = (record: DataType) => {
    setModalTitle(t('user.editUser'));
    setIsEdit(true);
    form.setFieldsValue({
      id: record.key,
      name: record.name,
      email: record.email,
      cellphone: record.cellphone,
      passwd: record.passwd,
      gender: record.gender,
      birthday: record.birthday ? dayjs(record.birthday) : null,
      status: record.status,
      department: record.department,
      position: record.position,
      wechat: record.wechat,
      address: record.address,
      remark: record.remark,
    });
    setIsModalOpen(true);
  };

  const handleStatusChange = (record: DataType) => {
    // Toggle: Normal(1) <-> Disabled(2), Locked(3) -> Normal(1)
    const newStatus = record.status === 1 ? 2 : 1;
    reqSave.caller({"id": record.key, "status": newStatus});
  };

  const onFinish = () => {
    form.validateFields().then((values) => {       
      const params = { ...values};
      // Convert birthday dayjs object to string format
      if (params.birthday) {
        params.birthday = dayjs(params.birthday).format('YYYY-MM-DD');
      }
      reqSave.caller(params);
    });
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
              <Form.Item label={t('user.name')} name="name" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('user.department')} name="department" style={{ marginBottom: 5 }}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label={t('user.position')} name="position" style={{ marginBottom: 5 }}>
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
            <Button type="primary" htmlType="submit" onClick={newUsers}>{t('user.newUser')}</Button>
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
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 18 }}
              initialValues={initialValues}
              onFinish={onFinish}>
              <Form.Item label="id" name="id" style={{ display: 'none' }}>
                <Input />
              </Form.Item>
              <Form.Item label={t('user.name')} name="name" rules={[{ required: true, message: t('user.namePlaceholder') }]}>
                <Input placeholder={t('user.namePlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.email')} name="email" rules={[{ required: true, message: t('user.emailPlaceholder') }]}>
                <Input placeholder={t('user.emailPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.cellphone')} name="cellphone" rules={[{ required: true, message: t('user.cellphonePlaceholder') }]}>
                <Input placeholder={t('user.cellphonePlaceholder')} />
              </Form.Item>
              {!isEdit && (
                <Form.Item label={t('user.password')} name="passwd" rules={[{ required: true, message: t('user.passwordPlaceholder') }]}>
                  <Input.Password placeholder={t('user.passwordPlaceholder')} autoComplete="new-password" />
                </Form.Item>
              )}
              <Form.Item label={t('user.gender')} name="gender">
                <Select placeholder={t('user.genderPlaceholder')} allowClear>
                  <Select.Option value="M">{t('user.genderMale')}</Select.Option>
                  <Select.Option value="F">{t('user.genderFemale')}</Select.Option>
                </Select>
              </Form.Item>
              <Form.Item label={t('user.birthday')} name="birthday">
                <DatePicker style={{ width: '100%' }} placeholder={t('user.birthdayPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.department')} name="department">
                <Input placeholder={t('user.departmentPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.position')} name="position">
                <Input placeholder={t('user.positionPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.wechat')} name="wechat">
                <Input placeholder={t('user.wechatPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.address')} name="address">
                <Input placeholder={t('user.addressPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.remark')} name="remark">
                <Input.TextArea placeholder={t('user.remarkPlaceholder')} />
              </Form.Item>
              <Form.Item label={t('user.status')} name="status" rules={[{ required: true, message: t('user.statusPlaceholder') }]}>
                <Select placeholder={t('user.statusPlaceholder')}>
                  <Select.Option value={1}>{t('user.statusNormal')}</Select.Option>
                  <Select.Option value={2}>{t('user.statusDisabled')}</Select.Option>
                  <Select.Option value={3}>{t('user.statusLocked')}</Select.Option>
                </Select>
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
