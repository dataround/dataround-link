/**
 * @author: yuehan124@gmail.com
 * @since: 2026/02/21
 * Resource Management Component
 **/
import { DeleteOutlined, EditOutlined, PlusOutlined, MinusOutlined } from "@ant-design/icons";
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
  Tag,
  Tooltip,
  message
} from "antd";
import { memo, useState, useEffect, forwardRef, useImperativeHandle } from "react";
import {
  deleteResource,
  getAllResources,
  saveOrUpdateResource
} from "../../api/permission";
import useRequest from "../../hooks/useRequest";
import { t } from "i18next";

interface ResourceApiType {
  id?: string;
  path: string;
  method: string;
}

interface ResourceDataType {
  key: string;
  pid: string;
  i18nName: string;
  name: string;
  resKey: string;
  resourceApis?: ResourceApiType[];
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
      form.setFieldsValue({ 
        id: '', 
        pid: '0', 
        i18nName: '', 
        resKey: '',
        resourceApis: [{ path: '', method: '' }]
      });
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
      title: t('permission.resourceKey'),
      dataIndex: "resKey",
      key: "resKey",
    },
    {
      title: 'APIs',
      key: "resourceApis",
      width: 400,
      render: (_, record) => {
        const apis = record.resourceApis || [];
        if (apis.length === 0) return '-';
        const display = apis.map(api => `${api.method} ${api.path}`).join(', ');
        const tags = apis.map((api, idx) => (
          <Tag key={idx} style={{ marginBottom: 2 }}>
            <span style={{ color: api.method === 'GET' ? '#52c41a' : api.method === 'POST' ? '#1890ff' : api.method === 'PUT' ? '#faad14' : '#ff4d4f', fontWeight: 500 }}>{api.method}</span>
            <span style={{ marginLeft: 4, maxWidth: 160, display: 'inline-block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', verticalAlign: 'bottom' }}>{api.path}</span>
          </Tag>
        ));
        return (
          <Tooltip title={<span style={{ fontSize: 12 }}>{display}</span>}>
            <div style={{ maxWidth: 380, overflow: 'hidden' }}>{tags}</div>
          </Tooltip>
        );
      },
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
        i18nName: item.i18nName,
        name: item.name || item.i18nName,
        resKey: item.resKey,
        resourceApis: item.resourceApis || [],
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

  const treeRequest = useRequest(getAllResources, {
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
    
    // Ensure resourceApis has at least one empty item if empty
    const resourceApis = record.resourceApis && record.resourceApis.length > 0 
      ? record.resourceApis 
      : [{ path: '', method: '' }];
    
    form.setFieldsValue({
      id: record.key,
      pid: record.pid,
      i18nName: record.i18nName,
      resKey: record.resKey,
      resourceApis: resourceApis,
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
      // Convert form data to ResourceRequest format
      const requestData = {
        id: values.id || null,
        pid: values.pid,
        i18nName: values.i18nName,
        resKey: values.resKey,
        resourceApis: values.resourceApis || []
      };
      saveRequest.caller(requestData);
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
            <Form.Item label={t('permission.resourceI18nName')} name="i18nName" rules={[{ required: true, message: t('permission.resourceI18nNamePlaceholder') }]}>
              <Input placeholder={t('permission.resourceI18nNamePlaceholder')} />
            </Form.Item>
            <Form.Item label={t('permission.resourceKey')} name="resKey" rules={[{ required: true, message: t('permission.resourceKeyPlaceholder') }]}>
              <Input placeholder={t('permission.resourceKeyPlaceholder')} />
            </Form.Item>
            <Form.Item label={t('permission.apiList')}>
              <Form.List name="resourceApis" rules={[{ validator: async (_, value) => {
                if (!value || value.length === 0) {
                  return Promise.reject(new Error('At least one API is required'));
                }
              }}]}>
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, ...restField }) => (
                      <Space key={key} style={{ display: 'flex', marginBottom: 4 }} align="baseline">
                        <Form.Item
                          {...restField}
                          name={[name, 'path']}
                          rules={[{ required: true, message: 'API Path is required' }]}
                          style={{ width: 250 }}
                        >
                          <Input placeholder="/api/user/list" />
                        </Form.Item>
                        <Form.Item
                          {...restField}
                          name={[name, 'method']}
                          rules={[{ required: true, message: 'Method is required' }]}
                          style={{ width: 120 }}
                        >
                          <Select placeholder="Method">
                            <Select.Option value="GET">GET</Select.Option>
                            <Select.Option value="POST">POST</Select.Option>
                            <Select.Option value="PUT">PUT</Select.Option>
                            <Select.Option value="DELETE">DELETE</Select.Option>
                          </Select>
                        </Form.Item>
                        {fields.length > 1 && <MinusOutlined onClick={() => remove(name)} />}
                        <PlusOutlined onClick={() => add()} />
                      </Space>
                    ))}
                  </>
                )}
              </Form.List>
            </Form.Item>
          </Form>
        </Card>
      </Modal>
    </>
  );
});

export default memo(ResourceManagement);
