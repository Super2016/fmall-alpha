package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserService {

    //登录服务,检验用户正确则返回前端以用户数据
    public ServerResponse<User> login(String username, String password);

    //注册服务,校验用户与邮箱是否存在,密码使用MD5码加密
    public ServerResponse<String> register(User user);

    //验证用户名和email是否存在
    public ServerResponse<String> checkValid(String str,String type);

    //[忘记密码]获取问题
    public ServerResponse forgetAndgetQuestion(String username);

    //[忘记密码]验证答案
    public ServerResponse<String> forgetAndVerifyAnswer(String username, String question, String answer);

    //[忘记密码]修改密码
    public ServerResponse<String> forgetAndResetPassword(String username,String newPassword,String forgetToken);

    //登录状态下修改密码
    public ServerResponse loginAndResetPassword(String oldPassword,String newPassword,User user);

    //更新用户信息
    public ServerResponse<User> updateUserInfo(User user);

    //获取用户详细信息
    public ServerResponse getUserDetailInfo(Integer userId);
}
