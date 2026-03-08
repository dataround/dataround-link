/**
 * @author: yuehan124@gmail.com
 * @since: 2026/02/21
 * Unified Permission Management Page with Tabs
 **/
import { DownOutlined, PlusOutlined } from "@ant-design/icons";
import { Button, Dropdown, Tabs, TabsProps } from "antd";
import { FC, memo, useState, useRef } from "react";
import { t } from "i18next";
import RoleManagement, { RoleRef } from "./role";
import UserRoleManagement from "./userRole";
import ResourceManagement, { ResourceRef } from "./resource";

interface IProps {}

const S: FC<IProps> = () => {
  const [activeTab, setActiveTab] = useState<string>("role");
  const roleRef = useRef<RoleRef>(null);
  const resourceRef = useRef<ResourceRef>(null);

  const newMenuItems = [
    {
      key: 'role',
      label: t('permission.newRole'),
      onClick: () => {
        setActiveTab('role');
        setTimeout(() => {
          roleRef.current?.newRole();
        }, 100);
      },
    },
    {
      key: 'resource',
      label: t('permission.newResource'),
      onClick: () => {
        setActiveTab('resource');
        setTimeout(() => {
          resourceRef.current?.newResource();
        }, 100);
      },
    },
  ];

  const mainTabs: TabsProps["items"] = [
    {
      key: "role",
      label: t('permission.roleManagement'),
      children: <RoleManagement ref={roleRef} visible={activeTab === 'role'} />,
    },
    {
      key: "userRole",
      label: t('permission.userRole'),
      children: <UserRoleManagement visible={activeTab === 'userRole'} />,
    },
    {
      key: "resource",
      label: t('permission.resourceManagement'),
      children: <ResourceManagement ref={resourceRef} visible={activeTab === 'resource'} />,
    },
  ];

  return (
    <div className="module">
      <Tabs 
        activeKey={activeTab} 
        items={mainTabs} 
        onChange={(key) => setActiveTab(key)}
        tabBarExtraContent={
          <Dropdown menu={{ items: newMenuItems }}>
            <Button type="primary">
              <PlusOutlined /> {t('common.new')} <DownOutlined />
            </Button>
          </Dropdown>
        }
      />
    </div>
  );
};

const Permission = memo(S);

export default Permission;
