package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import cn.fmall.service.ICategoryService;
import cn.fmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId, HttpSession session){
        //校验用户是否已登录
        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验管理员身份
        if (iUserService.checkAdminRole(currentUser).currentStatusIsSuccess()) {
            //调用分类服务处理分类逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        } else {
            return ServerResponse.createErrorResponseMsg("当前账户非管理员");
        }
    }
}
