package com.crayon.shiro.realm;

import com.crayon.pojo.user_manage.User;
import com.crayon.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 权限校验
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限校验--执行了doGetAuthorizationInfo...");
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        /*
         authorizationInfo.setRoles(usersService.findRoles(username));
        authorizationInfo.setStringPermissions(usersService.findPermissions(username));
         */
        //注入用户权限
        authorizationInfo.setRoles(userService.getRolesByUserName(username));
        authorizationInfo.setStringPermissions(userService.getPermissionsByUserName(username));
        return authorizationInfo;
    }

    /**
     * 身份校验
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException,IndexOutOfBoundsException{
        System.out.println("身份校验--执行了goGetAuthenticationInfo...");

        String username = (String) token.getPrincipal();

        User user = userService.getUserByName(username);

        if (user == null) {
            throw new UnknownAccountException(); //没有找到账号
        }
        //交给AuthenticationRealm使用CredentialsMatcher进行密码匹配
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUserName(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getSalt()), //salt=username+salt
                getName() //realm name
        );
        return authenticationInfo;
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