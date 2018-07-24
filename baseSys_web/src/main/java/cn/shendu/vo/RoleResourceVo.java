package cn.shendu.vo;

import cn.shendu.pojo.Resource;
import cn.shendu.pojo.Role;

import java.util.List;

public class RoleResourceVo {
    private Role role;
    private String resourceIds;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    @Override
    public String toString() {
        return "RoleResourceVo{" +
                "role=" + role +
                ", resourceIds='" + resourceIds + '\'' +
                '}';
    }
}
