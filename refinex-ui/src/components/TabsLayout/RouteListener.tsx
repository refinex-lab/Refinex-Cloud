import { useEffect } from 'react';
import { useLocation, useModel } from '@umijs/max';
import routes from '../../../config/routes';

// 扁平化路由配置
const flattenRoutes = (routeList: any[], parentPath = ''): any[] => {
  let result: any[] = [];
  routeList.forEach((route) => {
    const fullPath = route.path?.startsWith('/')
      ? route.path
      : `${parentPath}/${route.path || ''}`.replace(/\/+/g, '/');

    if (route.component && !route.layout && route.path !== '/user/*') {
      result.push({
        path: fullPath,
        name: route.name,
        title: route.name ? `menu.${parentPath.slice(1) ? `${parentPath.slice(1).replace(/\//g, '.')}.` : ''}${route.name}` : fullPath,
      });
    }

    if (route.routes) {
      result = result.concat(flattenRoutes(route.routes, fullPath));
    }
  });
  return result;
};

const RouteListener: React.FC = () => {
  const location = useLocation();
  const { addTab } = useModel('tabsModel');

  useEffect(() => {
    const { pathname } = location;

    // 排除不需要标签的路由
    const excludePaths = ['/user/login', '/user/register', '/user/forgot-password', '/user/register-result'];
    if (excludePaths.includes(pathname) || pathname === '/') {
      return;
    }

    // 获取扁平化的路由
    const flatRoutes = flattenRoutes(routes);

    // 查找匹配的路由
    const matchedRoute = flatRoutes.find((route) => {
      // 精确匹配
      if (route.path === pathname) {
        return true;
      }
      // 动态路由匹配 (例如 /kb/space/:spaceCode)
      const pattern = route.path.replace(/:[^/]+/g, '[^/]+');
      const regex = new RegExp(`^${pattern}$`);
      return regex.test(pathname);
    });

    if (matchedRoute) {
      addTab({
        key: pathname,
        path: pathname,
        title: matchedRoute.title || pathname,
      });
    } else {
      // 如果没有匹配到，使用路径作为标题
      addTab({
        key: pathname,
        path: pathname,
        title: pathname.split('/').pop() || pathname,
      });
    }
  }, [location.pathname, addTab]);

  return null;
};

export default RouteListener;

