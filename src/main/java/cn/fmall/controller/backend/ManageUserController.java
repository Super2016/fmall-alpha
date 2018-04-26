package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import cn.fmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台User控制器
 */
@Controller
@RequestMapping("/manage/user/")
public class ManageUserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username,String password,HttpSession session){
        ServerResponse<User> userResponse = iUserService.login(username,password);
        //登录成功
        if (userResponse.currentStatusIsSuccess()) {
            User user = userResponse.getData();
            //判断用户角色是管理员
            if (user.getRole() == Constant.Role.ROLE_ADMIN) {
                //当前是管理员账户
                session.setAttribute(Constant.CURRENT_USER,user);
                //返回成功响应
                return userResponse;
            }
            return ServerResponse.createErrorResponseMsg("当前用户非管理员");
        }
        return userResponse;
    }
}
