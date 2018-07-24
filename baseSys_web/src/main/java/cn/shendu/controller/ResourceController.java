package cn.shendu.controller;
import cn.shendu.pojo.Organization;
import cn.shendu.pojo.Resource;
import cn.shendu.pojo.TreeNode;
import cn.shendu.service.ResourceService;
import cn.shendu.util.FastJsonConvert;
import cn.shendu.util.ResponseUtils;
import cn.shendu.vo.ResourceVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/resource")
public class ResourceController extends BaseController<Resource>{

    @Autowired
    private ResourceService resourceService;

    private boolean getChild(Resource resource,ResourceVo resourceVo){
        boolean result = false;

        try {
            List<Resource> cList =
                    resourceService.list("parent_ids_like", resource.getParent_ids() + resource.getId() + "_");

            if(cList!=null && cList.size()>0){
                List<ResourceVo> arrList = new ArrayList<>();
                for(Resource r:cList){

                    ResourceVo rv = new ResourceVo();
                    rv.setId(r.getId()+"");
                    rv.setName(r.getName());
                    rv.setPermission(r.getPermission());
                    rv.setType(r.getType());
                    rv.setUrl(r.getUrl());


                    getChild(r,rv);
                    arrList.add(rv);
                }
                if(cList!=null && cList.size()>0){
                    resourceVo.setChildren(arrList);
                    resourceVo.setState("open");
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }




    @RequiresPermissions("resource:view")
    @RequestMapping(value = "list")
    public void list(HttpServletResponse response) throws Exception {

        List<ResourceVo> arrList = new ArrayList<>();
        try {
            List<Resource> oList = resourceService.list("parent_id", new Long(0));
            Resource r = oList.get(0);
            ResourceVo rv = new ResourceVo();
            rv.setId(r.getId()+"");
            rv.setName(r.getName());
            rv.setState("open");
            rv.setPermission(r.getPermission());
            rv.setType(r.getType());
            rv.setUrl(r.getUrl());
            getChild(r,rv);

            arrList.add(rv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseUtils.putTextResponse(response, FastJsonConvert.convertObjectToJSON(arrList));

    }


    private boolean getTreeChild(Resource resource,TreeNode treeNode,String resourceids){
        boolean result = false;

        try {
            List<Resource> cList =
                    resourceService.list("parent_ids_like", resource.getParent_ids() + resource.getId() + "_");

            if(cList!=null && cList.size()>0){
                List<TreeNode> arrList = new ArrayList<>();
                for(Resource r:cList){

                    TreeNode tr = new TreeNode();
                    tr.setId(r.getId()+"");
                    tr.setText(r.getName());

                    isCheck(tr,resourceids);
                    getTreeChild(r,tr,resourceids);

                    arrList.add(tr);
                }
                if(cList!=null && cList.size()>0){
                    treeNode.setChildren(arrList);
                    treeNode.setState("open");
                    isCheck(treeNode,resourceids);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private void isCheck(TreeNode treeNode,String resourceids){
        String id = treeNode.getId();
        if(StringUtils.contains(resourceids,id)){
            treeNode.setChecked("true");
        }
    }

    @RequestMapping(value = "getTree")
    public void getTree(HttpServletResponse response, HttpServletRequest request){
        String  resourceids = request.getParameter("resourceids");
        List<TreeNode> arrList = new ArrayList<>();
        try {
            List<Resource> oList = resourceService.list("parent_id", new Long(0));
            Resource r = oList.get(0);

            TreeNode tr = new TreeNode();
            tr.setId(r.getId()+"");
            tr.setText(r.getName());
            tr.setState("open");
            isCheck(tr,resourceids);
    //        tr.setChecked("false");

            getTreeChild(r,tr,resourceids);

            arrList.add(tr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseUtils.putTextResponse(response, FastJsonConvert.convertObjectToJSON(arrList));
    }

    private String getParentIdsByParentId(String parentId){
        List<String> arrayList = new ArrayList<String>();

        if(StringUtils.isNumeric(parentId)){
            arrayList.add(parentId);
        }

        for(int i=0;i<Integer.MAX_VALUE;i++){
            if(!StringUtils.isNumeric(parentId)){
                return null;
            }
            try {
                Resource resource = (Resource) resourceService.get(Long.parseLong(parentId));
                if(resource == null){
                    Collections.reverse(arrayList);
                    return String.join(",",arrayList);
                }else{
                    parentId = resource.getParent_id()+"";
                    arrayList.add(parentId);
                }
            } catch (Exception e) {
                Collections.reverse(arrayList);
                return String.join(",",arrayList)+",";
            }
        }

        Collections.reverse(arrayList);
        return String.join(",",arrayList)+",";
    }

    @RequiresPermissions("resource:create")
    @RequestMapping(value = "appendChild")
    public void create(Resource resource,HttpServletResponse response) throws Exception {
        logger.info("--------------------*---------------*------------------*---------------");
       // resource.
        resource.setParent_ids(getParentIdsByParentId(resource.getParent_id()+""));

        int key = resourceService.insertResource(resource);



        ResponseUtils.putTextResponse(response,key+"");
    }

    @RequiresPermissions("resource:update")
    @RequestMapping(value = "update")
    public void update(Resource resource,HttpServletResponse response) throws Exception {
        System.out.println("resource:......................."+resource);
        Resource r = (Resource) resourceService.get(resource.getId());

        System.out.println("resource:"+r);

        r.setName(resource.getName());
        r.setPermission(resource.getPermission());
        r.setType(resource.getType());
        r.setUrl(resource.getUrl());

        resourceService.update(r);

        ResponseUtils.putTextResponse(response,"success");
    }

    @RequiresPermissions("resource:delete")
    @RequestMapping(value = "delete")
    public void delete(Long id,HttpServletResponse response) throws Exception {

        // 先删除 子节点
        Resource r = (Resource) resourceService.get(id);

        List<Resource> cList =
                resourceService.list("parent_ids_like", r.getParent_ids() + r.getId() + "_");

        for(Resource resource : cList){
            resourceService.delete(resource);
        }



        resourceService.delete(r);

        ResponseUtils.putTextResponse(response,"success");
    }

}

