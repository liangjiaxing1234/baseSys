package cn.shendu.shiro.realm;

import cn.shendu.pojo.Resource;
import cn.shendu.pojo.Role;
import cn.shendu.pojo.User;
import cn.shendu.service.ResourceService;
import cn.shendu.service.RoleService;
import cn.shendu.service.UserService;
import cn.shendu.vo.RoleVo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    /*
     根据用户名  获取用户角色列表 roleList
     获取用户权限列表 permissionsList
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 最终判断逻辑是 WildcardPermission  implies   resource    [role *]

        String username = (String)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        RoleVo roleVo = userService.findRoles(username);
        Set<String> roleNameSet = roleVo.getRoleNameSet();
        Set<String> resourceNameSet = roleVo.getResourceNameSet();

        System.out.println("boolean:"+ resourceNameSet.contains("user:view"));

        authorizationInfo.setRoles(roleNameSet);
        authorizationInfo.setStringPermissions(resourceNameSet);




        return authorizationInfo;
    }



    /*
       获取用户名
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();


        try {
            List<User> userList = userService.list("username", username);
            User user = userList.get(0);

            if(user == null) {
                throw new UnknownAccountException();//没找到帐号
            }

            if(Boolean.TRUE.equals(false)) {
                throw new LockedAccountException(); //帐号锁定
            }

            //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    user.getUsername(), //用户名
                    user.getPassword(), //密码
                    ByteSource.Util.bytes(user.getUsername()+user.getSalt()),//salt=username+salt
                    getName()  //realm name
            );
            return authenticationInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;




    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
