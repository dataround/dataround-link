/**
 * @author: yuehan124@gmail.com
 * @since: 2025-09-22
 */
import React, { memo, useContext, useState } from "react";
import {
  Card, Form, Input, Button, Spin,
  Space,
  message
} from "antd";
import { useForm } from "antd/lib/form/Form";
import { useNavigate } from "react-router-dom";
import { t } from "i18next";

import "./index.less";
import { doLogin } from "../../api/login";
import useRequest from "../../hooks/useRequest";
import { baseAPI } from "../../utils";

export interface IGlobalContext {
  userInfo?: Record<string, any>;
  setUserInfo?: (info: Record<string, any>) => void;
}

const L = () => {
  const navigate = useNavigate();
  const [form] = useForm<{
    name: string;
    passwd: string;
  }>();

  const GlobalContext = React.createContext<IGlobalContext>({});
  const { setUserInfo } = useContext(GlobalContext);
  const [captchaUrl, setCaptchaUrl] = useState(`${baseAPI}/captcha?t=${Date.now()}`);

  const refreshCaptcha = () => {
    setCaptchaUrl(`${baseAPI}/captcha?t=${Date.now()}`);
  };

  const reqLogin = useRequest(doLogin, {
    wrapperFun: (res: any) => {
      sessionStorage.setItem("info", JSON.stringify(res));
      setUserInfo?.({ name: res?.name });
      navigate("/", { replace: true });
    },
  });

  const onLogin = async () => {
    form.validateFields().then((values) => {
      reqLogin.caller(values).then(() => {
        // login failed, refresh captcha
        refreshCaptcha();
      })
    });
  };

  return (
    <div className="login-layout">
      <Card className="login-card" title={t('login.title')}>
        <Spin spinning={reqLogin.loading}>
          <Form form={form}>
            <Form.Item name="name" label={t('login.username')} rules={[{ required: true, message: t('login.usernameRequired'), whitespace: true, max: 60 }]}>
              <Input placeholder={t('login.usernamePlaceholder')} onPressEnter={onLogin} autoComplete="off" />
            </Form.Item>
            <Form.Item name="passwd" label={t('login.password')} rules={[{ required: true, message: t('login.passwordRequired'), whitespace: true, max: 60 }]}>
              <Input.Password placeholder={t('login.passwordPlaceholder')} onPressEnter={onLogin} autoComplete="new-password" />
            </Form.Item>
            <Form.Item name="captcha" label={t('login.captcha')} rules={[{ required: true, message: t('login.captchaRequired'), whitespace: true, max: 60 }]}>
              <Space>
                <Input placeholder={t('login.captchaPlaceholder')} onPressEnter={onLogin} />
                <a><img style={{ marginTop: -15 }} onClick={refreshCaptcha} src={captchaUrl} alt="captcha" /></a>
              </Space>
            </Form.Item>
            <div style={{ textAlign: "center" }}>
              <Button type="primary" style={{ width: "100%" }} onClick={onLogin}>{t('login.loginButton')}</Button>
            </div>
          </Form>
        </Spin>
      </Card>
    </div>
  );
};

const Login = memo(L);
export default Login;
