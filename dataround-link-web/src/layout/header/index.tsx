/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 
 * @auth: tiandengji
 * @date: 2025/5/15
 **/
import { CheckOutlined, FolderOutlined, PoweroffOutlined, ProjectOutlined, QuestionCircleOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Col, Dropdown, Layout, Menu, MenuProps, Row, Space } from 'antd';
import { SubMenuType } from 'antd/es/menu/interface';
import { FC, memo, useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { doLogout } from '../../api/login';
import { getMyProjects, updateSelected } from '../../api/user';
import { getMenuItems, MenuItem } from '../../api/menu';
import useRequest from '../../hooks/useRequest';
import logoImage from '../../assets/logo.v20251009.png';
import LanguageSwitcher from '../../components/LanguageSwitcher';
import { useTranslation } from 'react-i18next';
import './index.less';

// eslint-disable-next-line @typescript-eslint/no-empty-interface
interface IProps { }

const { Header: AntHeader } = Layout;

const H: FC<IProps> = () => {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const { t } = useTranslation();
  let selected = '/batch/job';
  const [current, setCurrent] = useState(selected);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);

  useEffect(() => {
    getMenuItems().then((res) => {
      if (res?.data?.items) {
        setMenuItems(res.data.items);
      }
    }).catch((err) => {
      console.error('Failed to fetch menu items:', err);
    });
  }, []);

  const items: MenuProps['items'] = menuItems.map((item) => ({
    label: t(item.labelKey),
    key: item.key,
  })) as MenuProps['items'];

  const onClick: MenuProps['onClick'] = (e) => {
    setCurrent(e.key);
    const menuItem = menuItems.find((item) => item.key === e.key);
    if (menuItem?.external && menuItem.url) {
      window.open(menuItem.url, '_blank');
    } else if (menuItem?.url) {
      window.location.href = menuItem.url;
    }
  };
  
  useEffect(() => {
    projectReq.caller();
  }, [])

  const logoutReq = useRequest(doLogout, {
    wrapperFun: (resp: any) => {
      sessionStorage.removeItem('info');
      navigate('/login', { replace: true })
    }
  });

  const logout = () => {
    logoutReq.caller();
  }

  const userItemOptions: MenuProps['items'] = [
    {
      key: '1',
      label: '',
      children: [],
      onClick: (e: any) => {
        updateSelected(e.key).then((resp: any) => {
          window.location.reload();
        })
      }
    },
    {
      key: 'divider1',
      type: 'divider'
    },
    {
      key: '2',
      label: <><PoweroffOutlined style={{ color: '#ff4d4f' }} />  {t('menu.logout')}</>,
      onClick: () => logout()
    },
  ]

  const [userItems, setUserItems] = useState(userItemOptions);
  const projectReq = useRequest(getMyProjects, {
    wrapperFun: (data: any) => {
      const projects = new Array();
      let selectedProject = '';
      data.forEach((item: any) => {
        if (item.selected) {
          selectedProject = item.name;
        };
        projects.push({
          key: item.id,
          label: <>{item.selected ? <CheckOutlined style={{ marginLeft: 8, color: '#1890ff' }} /> : <span style={{ marginLeft: 8, width: 16, display: 'inline-block' }}></span>} &nbsp;&nbsp;{item.name}&nbsp;&nbsp;&nbsp;&nbsp; </>
        });
      })
      const item0 = userItemOptions[0] as SubMenuType;
      item0.label = <><FolderOutlined style={{ color: '#1890ff' }} />  {t('menu.project')}: {selectedProject}</>;
      item0.children = projects;
      setUserItems(userItemOptions);
    }
  });

  return (
    <AntHeader className="layout-header" style={{ padding: 0 }}>
      <Row>
        <Col span={4}>
          <a href='/'><img src={logoImage}></img></a>
        </Col>
        <Col span={12}>
          <Menu style={{ backgroundColor: 'transparent' }} onClick={onClick} selectedKeys={[current]} items={items} mode="horizontal">
          </Menu>
        </Col>
        <Col span={8} style={{ textAlign: 'right', paddingRight: '20px' }}>
          <div style={{float: 'right', marginRight: 35}}>
            <Dropdown menu={{ items: userItems }} overlayStyle={{ width: 210 }} placement="bottomRight" trigger={['hover']}>
              <span style={{ cursor: 'pointer', display: 'inline-block' }}>
                <Space align="center">
                  <UserOutlined />
                  <label>{t(`menu.user`)} </label>
                </Space>
              </span>
            </Dropdown>
          </div>       
          <LanguageSwitcher />    
        </Col>
      </Row>
    </AntHeader>
  );
};

const Header = memo(H);

export default Header;
