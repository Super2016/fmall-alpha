package cn.fmall.controller.portal;

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
 * 门户的User前端控制器
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    //声明的iUserService变量名与@Service("")值必须对应
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //调用service
        ServerResponse<User> userServerResponse = iUserService.login(username,password);
        //判断响应状态
        if (userServerResponse.currentStatusIsSuccess()) {
            //响应为成功,将用户数据填充进session域
            session.setAttribute(Constant.CURRENT_USER,userServerResponse.getData());
        }
        //返回响应给前台
        return userServerResponse;
    }

    /**
     * 注销登出
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(Constant.CURRENT_USER);
        //返回注销成功的状态码
        return ServerResponse.createSuccessResponse();
    }

    /**
     * 注销登出
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String > register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户名和邮箱是否存在,防止恶意用户通过分析接口调用注册接口
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户基本信息
     * 直接从session域中获取数据
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_base_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserBaseInfo(HttpSession session){
        //从session中获取user对象信息
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user != null) {
            //user对象不为空则返回user对象的信息给前端
            return ServerResponse.createSuccessResponse(user);
        }
        //user对象为空则表示未登录
        return ServerResponse.createErrorResponseMsg("用户未登录,无法获取任何用户信息");
    }

    /**
     * [忘记密码]拿到问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_and_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetAndGetQuestion(String username){
        return iUserService.forgetAndgetQuestion(username);
    }

    /**
     * [忘记密码]验证答案正确性
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_and_verify_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetAndVerifyAnswer(String username,String question,String answer){
        //调用service,传递数据给service验证
        return iUserService.forgetAndVerifyAnswer(username, question, answer);
    }

    /**
     * [忘记密码]修改密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_and_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetAndResetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetAndResetPassword(username,newPassword,forgetToken);
    }

    /**
     * 登录状态下重置修改密码
     * @param oldPassword
     * @param newPassword
     * @param session
     * @return
     */
    @RequestMapping(value = "login_and_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginAndResetPassword(String oldPassword,String newPassword,HttpSession session){
        //从sessin中获取当前已登录对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //验证用户是否已登录
        if (user == null) {
            ServerResponse.createErrorResponseMsg("用户未登录");
        }
        //用户已登录,业务开始
        return iUserService.loginAndResetPassword(oldPassword,newPassword,user);
    }

    /**
     * 更新用户基本信息
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "update_user_base_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserBaseInfo(User user,HttpSession session){
        User oldUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (oldUser == null) {
            return ServerResponse.createErrorResponseMsg("用户未登录");
        }
        user.setId(oldUser.getId());
        ServerResponse updateResponse = iUserService.updateUserInfo(user);
        if (updateResponse.currentStatusIsSuccess()) {
            //更新session
            session.setAttribute(Constant.CURRENT_USER,updateResponse.getData());
        }
        return updateResponse;

    }

    /**
     * 获取用户详细信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_detail_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getUserDetailInfo(HttpSession session){
        //从session中获取对象信息
        User currentUser = (User)session.getAttribute(Constant.CURRENT_USER);
        //如果检测用户状态未登录,则要求强制登录
        if (currentUser == null) {
            //如果传status=10给前端,前端需要做强制登录操作
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要强制登录,status=10");
        }

        //检测为用户已登录后,可取得信息
        return iUserService.getUserDetailInfo(currentUser.getId());
//        return ServerResponse.createSuccessResponse(session.getAttribute("currentUser"));
    }

}
