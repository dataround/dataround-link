/**
 * @author: yuehan124@gmail.com
 * @since: 2026/02/21
 * Resource Management Component
 **/
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
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
  message
} from "antd";
import { memo, useState, useEffect, forwardRef, useImperativeHandle } from "react";
import {
  deleteResource,
  getAllResources,
  getResourceTree,
  saveOrUpdateResource
} from "../../api/permission";
import useRequest from "../../hooks/useRequest";
import { t } from "i18next";

interface ResourceDataType {
  key: string;
  pid: string;
  name: string;
  enName: string;
  type: string;
  resKey: string;
  method: string;
  description: string;
  createTime: string;
  children?: ResourceDataType[];
}

interface ResourceProps {
  visible: boolean;
}

export interface ResourceRef {
  newResource: () => void;
}

const ResourceManagement = forwardRef<ResourceRef, ResourceProps>(({ visible }, ref) => {
  const [form] = Form.useForm();
  const [refresh, setRefresh] = useState<number>(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState<string>(t('permission.newResource'));
  const [data, setData] = useState<ResourceDataType[]>([]);
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [parentOptions, setParentOptions] = useState<any[]>([]);

  useImperativeHandle(ref, () => ({
    newResource: () => {
      setModalTitle(t('permission.newResource'));
      form.setFieldsValue({ id: '', pid: '0', name: '', enName: '', type: 'ui', resKey: '', method: '', description: '' });
      setIsModalOpen(true);
    }
  }));

  const columns: TableProps<ResourceDataType>["columns"] = [
    {
      title: t('permission.resourceName'),
      dataIndex: "name",
      key: "name",
    },
    {
      title: t('permission.resourceEnName'),
      dataIndex: "enName",
      key: "enName",
    },
    {
      title: t('permission.resourceType'),
      dataIndex: "type",
      key: "type",
      render: (type: string) => type === 'ui' ? t('permission.typeUi') : t('permission.typeApi'),
    },
    {
      title: t('permission.resourceKey'),
      dataIndex: "resKey",
      key: "resKey",
    },
    {
      title: t('permission.resourceMethod'),
      dataIndex: "method",
      key: "method",
    },
    {
      title: t('permission.resourceDesc'),
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
          <Popconfirm title={t('common.confirmDelete')} onConfirm={() => handleDelete(record)}>
            <Button type="link" style={{ padding: 0, gap: '4px' }}><DeleteOutlined />{t('common.delete')}</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const buildTreeData = (flatData: any[]): ResourceDataType[] => {
    const map = new Map<string, ResourceDataType>();
    const roots: ResourceDataType[] = [];

    flatData.forEach((item: any) => {
      const node: ResourceDataType = {
        key: String(item.id),
        pid: String(item.pid),
        name: item.name,
        enName: item.enName,
        type: item.type,
        resKey: item.resKey,
        method: item.method,
        description: item.description,
        createTime: item.createTime,
      };
      map.set(node.key, node);
    });

    flatData.forEach((item: any) => {
      const node = map.get(String(item.id))!;
      const parentId = String(item.pid);
      if (parentId === '0') {
        roots.push(node);
      } else {
        const parent = map.get(parentId);
        if (parent) {
          if (!parent.children) {
            parent.children = [];
          }
          parent.children.push(node);
        }
      }
    });

    return roots;
  };

  const formatData = (res: any) => {
    const flatData = res.data || res;
    const treeData = buildTreeData(flatData);
    setData(treeData);
    const allKeys = flatData.map((item: any) => String(item.id));
    setExpandedKeys(allKeys);
    return treeData;
  };

  const treeRequest = useRequest(getResourceTree, {
    wrapperFun: formatData,
  });

  const allResourcesForParentRequest = useRequest(getAllResources, {
    wrapperFun: (resData: any) => {
      const options = resData.map((r: any) => ({ label: r.name, value: String(r.id) }));
      options.unshift({ label: t('permission.rootResource'), value: '0' });
      setParentOptions(options);
      return options;
    },
  });

  useEffect(() => {
    if (visible) {
      treeRequest.caller();
      allResourcesForParentRequest.caller();
    }
  }, [visible, refresh]);

  const saveRequest = useRequest(saveOrUpdateResource, {
    wrapperFun: () => {
      message.success(t('permission.saveSuccess'));
      setIsModalOpen(false);
      setRefresh(Math.random);
    },
  });

  const handleEdit = (record: ResourceDataType) => {
    setModalTitle(t('permission.editResource'));
    form.setFieldsValue({
      id: record.key,
      pid: record.pid,
      name: record.name,
      enName: record.enName,
      type: record.type,
      resKey: record.resKey,
      method: record.method,
      description: record.description,
    });
    setIsModalOpen(true);
  };

  const handleDelete = (record: ResourceDataType) => {
    deleteRequest.caller(record.key);
  };

  const deleteRequest = useRequest(deleteResource, {
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

  const onExpand = (expanded: boolean, record: ResourceDataType) => {
    const key = record.key;
    if (expanded) {
      setExpandedKeys([...expandedKeys, key]);
    } else {
      setExpandedKeys(expandedKeys.filter(k => k !== key));
    }
  };

  return (
    <>
      <Spin spinning={treeRequest.loading}>
        <div style={{ marginTop: '16px' }}>
          <Table
            size="small"
            columns={columns}
            dataSource={data}
            pagination={false}
            expandable={{
              expandedRowKeys: expandedKeys,
              onExpand: onExpand,
            }}
          />
        </div>
      </Spin>

      {/* Resource Edit Modal */}
      <Modal title={modalTitle} open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={onFinish}
        cancelText={t('common.cancel')}
        okText={t('common.confirm')}
        width="45%"
      >
        <Card>
          <Form form={form} labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} onFinish={onFinish}>
            <Form.Item label="id" name="id" style={{ display: 'none' }}>
              <Input />
            </Form.Item>
            <Form.Item label={t('permission.parentResource')} name="pid">
              <Select options={parentOptions} placeholder={t('permission.selectParent')} />
            </Form.Item>
            <Form.Item label={t('permission.resourceName')} name="name" rules={[{ required: true, message: t('permission.resourceNamePlaceholder') }]}>
              <Input placeholder={t('permission.resourceNamePlaceholder')} />
            </Form.Item>
            <Form.Item label={t('permission.resourceEnName')} name="enName">
              <Input placeholder={t('permission.resourceEnNamePlaceholder')} />
            </Form.Item>
            <Form.Item label={t('permission.resourceType')} name="type" rules={[{ required: true, message: t('permission.resourceTypePlaceholder') }]}>
              <Select placeholder={t('permission.resourceTypePlaceholder')}>
                <Select.Option value="ui">{t('permission.typeUi')}</Select.Option>
                <Select.Option value="api">{t('permission.typeApi')}</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item label={t('permission.resourceKey')} name="resKey" rules={[{ required: true, message: t('permission.resourceKeyPlaceholder') }]}>
              <Input placeholder={t('permission.resourceKeyPlaceholder')} />
            </Form.Item>
            <Form.Item label={t('permission.resourceMethod')} name="method">
              <Select placeholder={t('permission.resourceMethodPlaceholder')} allowClear>
                <Select.Option value="GET">GET</Select.Option>
                <Select.Option value="POST">POST</Select.Option>
                <Select.Option value="PUT">PUT</Select.Option>
                <Select.Option value="DELETE">DELETE</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item label={t('permission.resourceDesc')} name="description">
              <Input.TextArea placeholder={t('permission.resourceDescPlaceholder')} />
            </Form.Item>
          </Form>
        </Card>
      </Modal>
    </>
  );
});

export default memo(ResourceManagement);
