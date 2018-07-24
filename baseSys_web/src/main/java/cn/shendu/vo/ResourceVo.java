package cn.shendu.vo;

import java.util.List;

public class ResourceVo {
    private String id;
    private String name;
    private String type;
    private String url;
    private String permission;

    private String state;

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    private List<ResourceVo> children;


    public ResourceVo() {
    }

    public ResourceVo(String id, String name, String type, String url, String permission) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.permission = permission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<ResourceVo> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceVo> children) {
        this.children = children;
    }
}

















