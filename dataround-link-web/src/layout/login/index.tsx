/**
 * @auth: tiandengji
 * @time: 2023/5/27
 * @func:
 * @params:
 * @return:
 * @updateTime:
 **/
import { FC, memo } from 'react';
import { Layout } from 'antd';

import './index.less';
import Login from '../../pages/login';
import LanguageSwitcher from '../../components/LanguageSwitcher';

interface IProps {}

const L: FC<IProps> = () => {

  return (
    <Layout className="layout">
      <div style={{ position: 'absolute', top: 10, right: 20 }}>
        <LanguageSwitcher />
      </div>
      <div className='login' style={{ flex: 1 }}>
        <Login></Login>
      </div>
      
    </Layout>
  );
};

const LoginLayout = memo(L);

export default LoginLayout;