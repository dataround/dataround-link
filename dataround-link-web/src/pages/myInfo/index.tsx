/**
 * @author: yuehan124@gmail.com
 * @since: 2025-09-22
 **/
import { Button, Descriptions, Form, Input, Tabs, Tag, message } from 'antd';
import TabPane from 'antd/es/tabs/TabPane';
import {
  FC,
  memo,
  useEffect,
  useState,
} from 'react';
import { getUserById, updatePasswd } from '../../api/user';
import useRequest from '../../hooks/useRequest';
import { t } from 'i18next';

interface IProps {
}

interface UserType {
  id: string;
  name: string;
  email: string;
  cellphone: string;
  passwd: string;
  avatar: string;
  gender: string;
  birthday: string;
  department: string;
  position: string;
  address: string;
  wechat: string;
  remark: string;
  status: number;
  lastLoginIp: string;
  lastLoginTime: string;
  createTime: string;
}

const S: FC<IProps> = () => {
  const [form] = Form.useForm();
  const [activeKey, setActiveKey] = useState('info');
  const [user, setUser] = useState<UserType>();

  useEffect(() => {
    infoRequest.caller();
    document.title = t('account.title');
  }, []);

  const infoRequest = useRequest(getUserById, {
    wrapperFun: (data: any) => {
      const user: UserType = {
        id: data.id,
        name: data.name,
        email: data.email,
        cellphone: data.cellphone,
        passwd: data.passwd,
        avatar: data.avatar,
        gender: data.gender,
        birthday: data.birthday,
        department: data.department,
        position: data.position,
        address: data.address,
        wechat: data.wechat,
        remark: data.remark,
        status: data.status,
        lastLoginIp: data.lastLoginIp,
        lastLoginTime: data.lastLoginTime,
        createTime: data.createTime,
      };
      setUser(user);
      return user;
    },
  });

  const onChange = (key: string) => {
    setActiveKey(key);
  };

  const onReset = () => {
    form.resetFields();
  };

  const updatePasswdRequest = useRequest(updatePasswd, {
    wrapperFun: (data: any) => {
      message.success(t('account.passwdUpdateSuccess'));
      form.resetFields();
    },
  });

  const onSubmit = () => {
    form.validateFields().then((values) => {
      if (values.newPwd !== values.newPwd2) {
        message.error(t('account.passwdMismatch'));
        return;
      }
      updatePasswdRequest.caller({
        oldPasswd: values.oldPwd,
        newPasswd: values.newPwd,
      });
    });
  };


  return (
    <div className="module">
      <Tabs activeKey={activeKey} onChange={onChange}>
        <TabPane tab={t('account.info')} key="info">
          <Descriptions title="" column={3}>
            <Descriptions.Item label={t('account.name')}>{user?.name}</Descriptions.Item>
            <Descriptions.Item label={t('account.cellphone')}>{user?.cellphone}</Descriptions.Item>
            <Descriptions.Item label={t('account.email')}>{user?.email}</Descriptions.Item>
            <Descriptions.Item label={t('account.department')}>{user?.department}</Descriptions.Item>
            <Descriptions.Item label={t('account.position')}>{user?.position}</Descriptions.Item>
            <Descriptions.Item label={t('account.gender')}>{user?.gender === 'M' ? t('user.genderMale') : user?.gender === 'F' ? t('user.genderFemale') : user?.gender}</Descriptions.Item>
            <Descriptions.Item label={t('account.birthday')}>{user?.birthday}</Descriptions.Item>
            <Descriptions.Item label={t('account.wechat')}>{user?.wechat}</Descriptions.Item>
            <Descriptions.Item label={t('account.status')}>
              <Tag color={user?.status === 1 ? 'green' : user?.status === 2 ? 'red' : 'orange'}>
                {user?.status === 1 ? t('user.statusNormal') : user?.status === 2 ? t('user.statusDisabled') : t('user.statusLocked')}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label={t('account.address')} span={3}>{user?.address}</Descriptions.Item>
            <Descriptions.Item label={t('account.remark')} span={3}>{user?.remark}</Descriptions.Item>
            <Descriptions.Item label={t('account.lastLoginIp')}>{user?.lastLoginIp}</Descriptions.Item>
            <Descriptions.Item label={t('account.lastLoginTime')}>{user?.lastLoginTime}</Descriptions.Item>
            <Descriptions.Item label={t('account.createTime')}>{user?.createTime}</Descriptions.Item>
          </Descriptions>
        </TabPane>

        <TabPane tab={t('account.edit')} key="edit">
          <Form layout="horizontal" form={form} style={{ marginBottom: '0px' }} labelCol={{ span: 3 }} wrapperCol={{ span: 5 }}>
            <Form.Item name="oldPwd" label={t('account.oldPwd')} rules={[{ required: true, message: t('account.oldPwdRequired') }]}>
              <Input.Password autoComplete="off" />
            </Form.Item>
            <Form.Item name="newPwd" label={t('account.newPwd')} rules={[{ required: true, min: 8, message: t('account.newPwdRequired') }]}>
              <Input.Password autoComplete="new-password" />
            </Form.Item>
            <Form.Item name="newPwd2" label={t('account.newPwd2')} rules={[{ required: true, message: t('account.newPwd2Required') }]}>
              <Input.Password autoComplete="new-password" />
            </Form.Item>
            <Form.Item wrapperCol={{ offset: 3, span: 6 }}>
              <Button htmlType="button" onClick={onReset} style={{ marginRight: 20 }}>{t('account.reset')}</Button>
              <Button type="primary" htmlType="submit" onClick={onSubmit} loading={updatePasswdRequest.loading}>{t('account.submit')}</Button>
            </Form.Item>
          </Form>
        </TabPane>
      </Tabs>
    </div>
  );
};

const UserInfo = memo(S);

export default UserInfo;
