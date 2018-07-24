package cn.shendu.controller;
import cn.shendu.pojo.Resource;
import cn.shendu.pojo.Role;
import cn.shendu.pojo.base.BasePOJO;
import cn.shendu.service.ResourceService;
import cn.shendu.service.RoleService;
import cn.shendu.util.FastJsonConvert;
import cn.shendu.util.Pagination;
import cn.shendu.util.ResponseUtils;
import cn.shendu.vo.ResourceVo;
import cn.shendu.vo.RoleResourceVo;
import com.alibaba.fastjson.JSONObject;
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
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController<Role>{

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping("toCreateForm")
    public String toCreateForm(){
        return "html/role/createForm.html";
    }

    private RoleResourceVo getRoleResourceVoByRoleId(Long id){
        try{
        Role role = (Role)roleService.get(id);
        String resource_ids = role.getResource_ids();
        RoleResourceVo roleResourceVo = new RoleResourceVo();
        roleResourceVo.setResourceIds(resource_ids);
        roleResourceVo.setRole(role);

        return roleResourceVo;
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    @RequestMapping("toUpdateForm")
    public String toUpdateForm(Long id, HttpSession session){

        try {
            session.setAttribute("roleResourceVo",getRoleResourceVoByRoleId(id));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "html/role/updateForm.html";
    }

    @RequestMapping("toReourceForm")
    public String toReourceForm(Long id, HttpSession session){

        try {
            session.setAttribute("roleResourceVo",getRoleResourceVoByRoleId(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "html/role/tree.html";
    }


    @RequestMapping("getRoleDataBySession")
    public void getRoleDataBySession(HttpSession session,HttpServletResponse response){
        RoleResourceVo roleResourceVo = (RoleResourceVo)session.getAttribute("roleResourceVo");

        JSONObject jsonObject = FastJsonConvert.convertObjectToJSONObject(roleResourceVo.getRole());
        jsonObject.put("resourceIds",roleResourceVo.getResourceIds());

        ajaxJson(response,jsonObject);
    }


    @RequiresPermissions("role:view")
    @RequestMapping("list")
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String page=request.getParameter("page");
        String rows=request.getParameter("rows");

        if(page==null){
            page="1";
        }
        if(rows==null){
            rows="2";
        }

        String roleName=request.getParameter("role");

        Pagination pagination = new Pagination();

        Integer rowsIntVal = Integer.parseInt(rows);
        Integer pageIntVal = Integer.parseInt(page);

        pagination.setCount(rowsIntVal);
        pagination.setStart((pageIntVal-1)*rowsIntVal);

        List<Role> list = null;
        if(StringUtils.isNotBlank(roleName)){
             list = roleService.list("role_like", roleName, "pagination", pagination);
        }else{
             list = roleService.list("pagination", pagination);
        }

        JSONObject jo = new JSONObject();


        jo.put("total",pagination.getTotal());
        jo.put("rows",list);


       ajaxJson(response,jo);
    }



    @RequiresPermissions("role:create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(Role role,HttpServletResponse response) throws Exception {
        roleService.add(role);
        ajaxJson(response,"success");
    }

    @RequiresPermissions("role:update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(Role role,HttpServletRequest request) throws Exception {
        roleService.update(role);
    }


    @RequiresPermissions("role:delete")
    @RequestMapping(value = "delete")
    public void delete(String ids,HttpServletResponse response) throws Exception {
        logger.info(".....................................................");
        SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

        String format = aDate.format(System.currentTimeMillis());

        String sql = "UPDATE sys_role set deleteAt= '"+format+"' WHERE id IN ("+ids+")";

        System.out.println("sql:"+ sql );
        roleService.updateBysql(sql);

        ResponseUtils.putTextResponse(response,"1");
    }



}