package cn.shendu.controller;
import cn.shendu.annotation.LogAnnotation;
import cn.shendu.pojo.Organization;
import cn.shendu.pojo.Role;
import cn.shendu.pojo.User;
import cn.shendu.pojo.base.BasePOJO;
import cn.shendu.service.OrganizationService;
import cn.shendu.service.RoleService;
import cn.shendu.service.UserService;
import cn.shendu.service.impl.PasswordHelper;
import cn.shendu.util.Constants;
import cn.shendu.util.FastJsonConvert;
import cn.shendu.util.Pagination;
import cn.shendu.util.ResponseUtils;
import cn.shendu.vo.RoleVo;
import cn.shendu.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController<User>{

    @Autowired
    private UserService userService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordHelper passwordHelper;


    private String getRoleNamesByIds(String ids){
        try {
          return  userService.selectRoleNamesByIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping("getRole")
    public void getRole(HttpServletRequest request,HttpServletResponse response){
        User user = (User) request.getAttribute(Constants.CURRENT_USER);

        String role_ids = user.getRole_ids();

        List<String> roleIds = Arrays.asList(StringUtils.split(role_ids, ","));

        try {
            List roleList = roleService.list("id in", roleIds);

            ResponseUtils.putTextResponse(response,FastJsonConvert.convertObjectToJSON(roleList));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private String getOrganizationStr(Organization organization) throws Exception {
        String parent_ids = organization.getParent_ids();

        //parent_ids = StringUtils.substring(parent_ids,0,StringUtils.length(parent_ids)-2);

        List<Organization> oList =
                organizationService.list("id in", Arrays.asList(StringUtils.split(parent_ids,"," )));

        if(oList!=null){

            oList.sort(new Comparator<Organization>() {
                @Override
                public int compare(Organization o1, Organization o2) {
                    if(o1.getId()>o2.getId()){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            });

            String organizationStrs = "";
            for(Organization o:oList){
                if(StringUtils.equalsIgnoreCase("",organizationStrs)){
                    organizationStrs = organizationStrs + o.getName();
                }else{
                    organizationStrs = organizationStrs+"->"+o.getName();
                }
            }
            if(StringUtils.equalsIgnoreCase("",organizationStrs)){
                organizationStrs = organizationStrs + organization.getName();
            }else{
                organizationStrs = organizationStrs+"->"+organization.getName();
            }

            return organizationStrs;
        }
        return  null;
    }


    @RequiresPermissions("user:view")
    @RequestMapping("list")
    public void list(HttpServletResponse response, HttpServletRequest request) throws Exception {

        System.out.println("user list................................");

        String page=request.getParameter("page");
        String rows=request.getParameter("rows");

        if(page==null){
            page="1";
        }
        if(rows==null){
            rows="2";
        }

        String username=request.getParameter("username");

        Pagination pagination = new Pagination();

        Integer rowsIntVal = Integer.parseInt(rows);
        Integer pageIntVal = Integer.parseInt(page);

        pagination.setCount(rowsIntVal);
        pagination.setStart((pageIntVal-1)*rowsIntVal);

        String limitCondition = pagination.getStart()+","+pagination.getCount();

        List<User> list = userService.list("username_like", username, "pagination", pagination);

        List<Map<String,String>> mapList = new ArrayList<>();
        for(User user : list){
            Long organization_id = user.getOrganization_id();
            Organization organization = (Organization)organizationService.get(organization_id);
            String organizationStr = getOrganizationStr(organization);

            Map<String,String> map = new HashMap<>();
            map.put("id",user.getId()+"");
            map.put("username",user.getUsername());
            map.put("role_ids",user.getRole_ids());

            map.put("oid",user.getOrganization_id()+"");

            map.put("organization_name",organizationStr);

            String role_ids = user.getRole_ids();
            String roleNames = getRoleNamesByIds(role_ids);
            map.put("roleNames",roleNames);
            mapList.add(map);
        }

        JSONObject jo = new JSONObject();

        if(mapList!=null){
            jo.put("total",mapList.size());
            jo.put("rows",mapList);

        }

        ajaxJson(response,jo);

    }


    @RequestMapping(value = "toCreateForm")
    public String toCreateForm(){
        return "html/user/createForm.html";
    }


    @RequestMapping(value = "getRoleList")
    public void getRoleList(HttpServletResponse response){
        try {
            List<Role> roleList = roleService.list();
            ResponseUtils.putTextResponse(response, FastJsonConvert.convertObjectToJSON(roleList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @LogAnnotation(moduleName="userModule",operate="create")
    @RequiresPermissions("user:create")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public void create(User user, @RequestParam(value = "roleid[]", required = false) String[] roleids) throws Exception {
        passwordHelper.encryptPassword(user);
        user.setRole_ids(String.join(",",roleids));

        userService.add(user);
    }



    private UserVo getUserVo(Long id){
        UserVo userVo = new UserVo();
        try {
            User user = (User)userService.get(id);
            Organization organization = (Organization)organizationService.get(user.getOrganization_id());
            userVo.setId(user.getId()+"");


            userVo.setOrganizationName(organization.getName());




            userVo.setOrganization_id(user.getOrganization_id()+"");

            userVo.setName(user.getUsername());
            String roleIds = user.getRole_ids();
            List<String> list = Arrays.asList(StringUtils.split(roleIds, ","));
            List<Role> hasroleList = roleService.list("id in", list);

            List<Role> hasNotRoleList = roleService.list("id not in",list);


            userVo.setHasRoleList(hasroleList);
            userVo.setNoHasRoleList(hasNotRoleList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userVo;
    }

    @RequestMapping("getUserDataBySession")
    public void getUserDataBySession(HttpSession session,HttpServletResponse response){
        UserVo userVo =  (UserVo)session.getAttribute("userVo");
        ResponseUtils.putTextResponse(response,FastJsonConvert.convertObjectToJSON(userVo));
    }

    @RequestMapping("toUpdateForm")
    public String toUpdateForm(Long id, HttpSession session){

        try {
            UserVo userVo = getUserVo(id);
            System.out.println(userVo);
            session.setAttribute("userVo",userVo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "html/user/updateForm.html";
    }



    @RequiresPermissions("user:update")
    @RequestMapping(value = "update")
    public void update(HttpServletResponse response,User user, @RequestParam(value = "roleid[]", required = false) String[] roleids) throws Exception {
        User u = (User)userService.get(user.getId());

        u.setUsername(user.getUsername());
        u.setOrganization_id(user.getOrganization_id());
        u.setRole_ids(String.join(",",roleids));

        userService.update(u);

        ResponseUtils.putTextResponse(response,"success");

    }

    @RequestMapping(value = "toChangPwdForm")
    public String toChangPwdForm(Long id, HttpSession session){
        session.setAttribute("id",id+"");
        return "html/user/changPwdForm.html";
    }


    @RequestMapping("getChangPwdBySession")
    public void getChangPwdBySession(HttpSession session,HttpServletResponse response){
        String id = (String)session.getAttribute("id");
        ResponseUtils.putTextResponse(response,id);
    }

    @RequestMapping("changPwd")
    public void changPwd(User user,HttpServletResponse response){

        try {
            User u = (User)userService.get(user.getId());
            u.setPassword(user.getPassword());
            passwordHelper.encryptPassword(u);

            userService.update(u);
            ResponseUtils.putTextResponse(response,"1");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @RequiresPermissions("user:delete")
    @RequestMapping(value = "delete")
    public void delete(String ids,HttpServletResponse response) throws Exception {
        logger.info(".....................................................");
        SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

        String format = aDate.format(System.currentTimeMillis());

        String sql = "UPDATE sys_user set deleteAt= '"+format+"' WHERE id IN ("+ids+")";

        System.out.println("sql:"+ sql );
        roleService.updateBysql(sql);

        ResponseUtils.putTextResponse(response,"1");
    }







}