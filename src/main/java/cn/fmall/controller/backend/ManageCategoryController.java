package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Category;
import cn.fmall.pojo.User;
import cn.fmall.service.ICategoryService;
import cn.fmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 产品类别控制器
 */
@Controller
@RequestMapping("/manage/category/")
public class ManageCategoryController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加新分类
     * @param categoryName
     * @param parentId
     * @param session
     * @return
     */
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value = "parentId",defaultValue = "0") int parentId,String categoryName,HttpSession session){
        //校验用户是否已登录
        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验管理员身份
        if (iUserService.checkAdminRole(currentUser).currentStatusIsSuccess()) {
            //调用分类服务处理分类逻辑,添加类别
            return iCategoryService.addCategory(categoryName,parentId);
        } else {
            return ServerResponse.createErrorResponseMsg("当前账户非管理员");
        }
    }

    /**
     * 更新categoryname分类名称
     * @param categoryId
     * @param categoryName
     * @param session
     * @return
     */
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId,String categoryName,HttpSession session){
        User user = (User)session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createErrorResponseMsg("用户未登录");
        }
        //当前登录的是管理员账户
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
           //调用分类服务处理分类逻辑,修改分类名称
            return iCategoryService.setCategoryName(categoryId,categoryName);
        } else {
            return ServerResponse.createErrorResponseMsg("当前账户非管理员");
        }
    }

    /**
     * 获得平级的子节点的信息
     * @param categoryId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_child_category_parallel.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildCategoryParallel(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId, HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createErrorResponseMsg("用户为登录");
        }
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //获取平级子节点
            return iCategoryService.getChildCategoryParallel(categoryId);
        } else {
            return ServerResponse.createErrorResponseMsg("当前用户非管理员");
        }

    }


    /**
     * 查询当前节点与所有树形子节点的id
     * @param categoryId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_child_category_recursion.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildCategoryRecursion(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //检验用户是否登录
        if (user == null) {
            return ServerResponse.createErrorResponseMsg("用户未登录");
        }
        //检验管理员用户
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //递归查询当前节点下的所有树形子节点
            return iCategoryService.getChildCategoryRecursion(categoryId);
        } else {
            return ServerResponse.createErrorResponseMsg("当前用户非管理员账户");
        }
    }






}
