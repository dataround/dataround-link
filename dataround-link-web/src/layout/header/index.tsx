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
import { CheckOutlined, EditOutlined, FolderOutlined, PoweroffOutlined, QuestionCircleOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Col, Dropdown, Layout, Menu, MenuProps, Row } from 'antd';
import { FC, memo, useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import useRequest from '../../hooks/useRequest';
import './index.less';
import logoImage from '/public/logo.v20250728.png';
import LanguageSwitcher from '../../components/LanguageSwitcher';
import { useTranslation } from 'react-i18next';
//import { ItemType, MenuItemGroupType, MenuItemType, SubMenuType } from 'antd/es/menu/interface';

// eslint-disable-next-line @typescript-eslint/no-empty-interface
interface IProps { }

const { Header: AntHeader } = Layout;

type MenuItem = Required<MenuProps>['items'][number];

const H: FC<IProps> = () => {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const { t } = useTranslation();
  let selected = '/batch/job';
  const [current, setCurrent] = useState(selected);

  const items: MenuItem[] = [
    {
      label: t('menu.dataIntegration'),
      key: '/'
    },
    {
      label: t('menu.doc'),
      key: 'doc'
    }
  ];

  const onClick: MenuProps['onClick'] = (e) => {
    setCurrent(e.key);
    console.log(e.key);
    if (e.key === 'doc')
      window.open('https://dataround.io/doc', '_blank');
  };

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
          <LanguageSwitcher />
        </Col>
      </Row>
    </AntHeader>
  );
};

const Header = memo(H);

export default Header;
