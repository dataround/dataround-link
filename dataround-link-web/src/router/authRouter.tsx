/**
 * @author: yuehan124@gmail.com
 * @since: 2025-09-22
 **/
import { Navigate } from "react-router-dom";
import { ReactNode, useEffect, useState, useRef } from "react";
import { setResources, Resource } from "../store";
import { getUserResources } from "../api/permission";

const isExpired = (expireTime: number) => {
  const currentTime = Date.now();
  return currentTime > expireTime;
};

const getUserInfo = () => {
  const info = sessionStorage.getItem("info");
  if (info) {
    try {
      const tokenInfo = JSON.parse(info);
      if (!isExpired(tokenInfo.expire)) {
        return tokenInfo;
      }
    } catch (e) {
      console.error("parse token error", e);
    }
  }
  return null;
};

function AuthRouter({ children }: { children: ReactNode }) {
  const [loading, setLoading] = useState(true);
  const loadedRef = useRef(false);

  useEffect(() => {
    // Only load once
    if (loadedRef.current) {
      return;
    }
    
    const userInfo = getUserInfo();
    if (userInfo) {
      loadedRef.current = true;
      // Load user resources on auth check
      const resourcesStr = sessionStorage.getItem("resources");
      if (resourcesStr) {
        try {
          const resources = JSON.parse(resourcesStr);
          setResources(resources);
        } catch (e) {
          console.error("parse resources error", e);
        }
        setLoading(false);
      } else {
        // Fetch resources from server
        getUserResources()
          .then((res: any) => {
            const resources: Resource[] = res.data || [];
            sessionStorage.setItem("resources", JSON.stringify(resources));
            setResources(resources);
          })
          .catch((e) => {
            console.error("Failed to load resources", e);
          })
          .finally(() => {
            setLoading(false);
          });
      }
    } else {
      setLoading(false);
    }
  }, []); // Empty dependency array - only run once

  const userInfo = getUserInfo();
  if (!userInfo) {
    return <Navigate to={"/login"} replace={true}></Navigate>;
  }

  if (loading) {
    return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>Loading...</div>;
  }

  return <>{children}</>;
}

export { AuthRouter };
