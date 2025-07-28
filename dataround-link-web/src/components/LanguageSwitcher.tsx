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
 * @description: LanguageSwitcher
 * @author: yuehan124@gmail.com
 * @date: 2026-06-05
 */
import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Dropdown, MenuProps, Space } from 'antd';
import { GlobalOutlined } from '@ant-design/icons';

const LanguageSwitcher: React.FC = () => {
  const { i18n, t } = useTranslation();
  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
  };
  const itemOptions: MenuProps['items'] = [
    {
      key: 'en',
      label: 'English',
      onClick: () => changeLanguage('en')
    },
    {
      key: 'zh',
      label: '简体中文',
      onClick: () => changeLanguage('zh')
    },
  ]
  const [items, setItems] = useState(itemOptions);

  return (
    <>
      <div className="user-center" style={{ float: 'right', marginRight: 35 }}>
        <Dropdown menu={{ items: itemOptions }} overlayStyle={{ width: 210 }} placement="bottomRight" trigger={['hover']}>
          <span style={{ cursor: 'pointer', display: 'inline-block' }}>
            <Space align="center">
              <GlobalOutlined />
              <label>{t(`language.${i18n.language}`)}</label>
            </Space>
          </span>
        </Dropdown>
      </div>
    </>
  );
};

export default LanguageSwitcher; 