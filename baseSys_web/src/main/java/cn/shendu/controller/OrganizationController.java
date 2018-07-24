package cn.shendu.controller;
import cn.shendu.pojo.Organization;
import cn.shendu.pojo.TreeNode;
import cn.shendu.pojo.base.BasePOJO;
import cn.shendu.service.OrganizationService;
import cn.shendu.util.FastJsonConvert;
import cn.shendu.util.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.reflect.generics.tree.Tree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/organization")
public class OrganizationController   extends BaseController<Organization>{

    @Autowired
    private OrganizationService organizationService;

    @RequiresPermissions("organization:view")
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        return "jsp/organization/index";
    }


    private boolean getChild(Organization o,TreeNode rootNode){
        boolean result = false;

        try {
            List<Organization> cList =
                    organizationService.list("parent_ids_like", o.getParent_ids() + o.getId() + "_");

            if(cList!=null && cList.size()>0){
                List<TreeNode> arrList = new ArrayList<>();
                for(Organization organization:cList){
                    TreeNode t = new TreeNode(organization.getId()+"",organization.getName());
                    getChild(organization,t);
                    arrList.add(t);
                }
                rootNode.setChildren(arrList);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }



    @RequiresPermissions("organization:view")
    @RequestMapping(value = "/tree")
    public void showTree(HttpServletResponse response)  {
        List<TreeNode> arrList = new ArrayList<>();
        try {
            List<Organization> oList = organizationService.list("parent_id", new Long(0));
            Organization o = oList.get(0);
            TreeNode rootNode = new TreeNode(o.getId()+"",o.getName(),"open");
            getChild(o,rootNode);

            arrList.add(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseUtils.putTextResponse(response,FastJsonConvert.convertObjectToJSON(arrList));

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
                Organization organization = (Organization) organizationService.get(Long.parseLong(parentId));
                if(organization == null){
                    Collections.reverse(arrayList);
                    return String.join(",",arrayList);
                }else{
                    parentId = organization.getParent_id()+"";
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




    @RequiresPermissions("organization:create")
    @RequestMapping(value = "/appendChild")
    public void create(String parentId,String nodeText,HttpServletResponse response) throws Exception {

        logger.info("-----------------------------------------------------------------");
        logger.info("parentId:"+parentId);
        logger.info("nodeText:"+nodeText);
        Organization organization = new Organization();
        organization.setParent_id(Long.parseLong(parentId));
       // logger.info("getParentIdsByParentId:"+getParentIdsByParentId(parentId));
        organization.setParent_ids(getParentIdsByParentId(parentId));
        organization.setName(nodeText);

        logger.info("organization:"+organization);

        Long key = organizationService.insertOrganization(organization);
        logger.info("key:"+key);

        ResponseUtils.putTextResponse(response,key+"");
    }

    @RequiresPermissions("organization:update")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(Organization organization,HttpServletResponse response) throws Exception {

        Organization o = (Organization)organizationService.get(organization.getId());

        o.setName(organization.getName());

        organizationService.update(o);
        ResponseUtils.putTextResponse(response,"success");
    }

    @RequiresPermissions("organization:delete")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void delete(Long id,HttpServletResponse response) throws Exception {

        Organization  o = (Organization)organizationService.get(id);

        List<Organization> cList =
                organizationService.list("parent_ids_like", o.getParent_ids() + o.getId() + "_");

        for(Organization organization : cList){
            organizationService.delete(organization);
        }


        organizationService.delete(o);

        ResponseUtils.putTextResponse(response,"success");

    }




    @RequiresPermissions("organization:view")
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String success() {
        return "jsp/organization/success.jsp";
    }

}