package cn.fmall.service.imp;

import cn.fmall.common.Constant;
import cn.fmall.common.ServerResponse;
import cn.fmall.common.TokenCache;
import cn.fmall.dao.UserMapper;
import cn.fmall.pojo.User;
import cn.fmall.service.IUserService;
import cn.fmall.utils.Md5Utils;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户服务
 */
@Service("iUserService")
public class UserServiceImp implements IUserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录服务,检验用户正确则返回前端以用户数据
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username,String password){
        //检查用户名是否存在
        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createErrorResponseMsg("用户名不存在");
        }
        //数据库存储密码使用MD5加密
        String md5Password = Md5Utils.Md5EncodeUtf8(password);

        User user = userMapper.selectLoginUser(username,md5Password);
        if (user == null) {
            return ServerResponse.createErrorResponseMsg("用户名或密码不正确");
        }
        //返回响应中无需返回密码,这里将密码置空
        user.setPassword(StringUtils.EMPTY);
        //返回给前台以消息与数据
        return ServerResponse.createSuccessResponse("登录成功",user);
    }

    /**
     * 注册服务,校验用户与邮箱是否存在,密码使用MD5码加密
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user){

        //验证用户名是否存在
        ServerResponse validUser = this.checkValid(user.getUsername(),Constant.USERNAME);
        if (!validUser.currentStatusIsSuccess()) {
            return ServerResponse.createErrorResponseMsg("用户名已存在");
        }

        //验证邮箱是否存在
        ServerResponse validEmail = this.checkValid(user.getEmail(),Constant.EMAIL);
        if (!validEmail.currentStatusIsSuccess()) {
            return ServerResponse.createErrorResponseMsg("邮箱已存在");
        }

        //默认设置注册账户为普通用户
        user.setRole(Constant.Role.ROLE_CUSTOMER);

        //专门使用MD5加密后再存入user对象
        user.setPassword(Md5Utils.Md5EncodeUtf8(user.getPassword()));

        int count = userMapper.insert(user);
        //影响行数
        if (count == 0) {
            return ServerResponse.createErrorResponseMsg("注册失败,请重试");
        }
        return ServerResponse.createSuccessResponseMsg("注册成功");
    }

    /**
     * 校验用户名与邮箱
     * 在注册时查询到不存在用户名或邮箱即通过
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        //当已指定校验的类型时,开始校验
        if (StringUtils.isNotBlank(type)) {
            //当指定验证的是用户名时
            if (type.equals(Constant.USERNAME)) {
                int count = userMapper.checkUsername(str);
                if (count > 0) {
                    return ServerResponse.createErrorResponseMsg("用户名已存在");
                }
            }
            //当指定验证的是email时
            if (type.equals(Constant.EMAIL)) {
                int count = userMapper.checkEmail(str);
                if (count > 0) {
                    return ServerResponse.createErrorResponseMsg("邮箱已存在");
                }
            }
        } else {
            //接收type为空,未指定type
            return ServerResponse.createErrorResponseMsg("参数错误,未指定需校验的类型");
        }
        //校验通过,返回成功状态码与消息
        return ServerResponse.createSuccessResponseMsg("校验通过");
    }

    /**
     * [忘记密码]获取问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse forgetAndgetQuestion(String username){
        //首先查询是否存在username
        ServerResponse validResponse = this.checkValid(username,Constant.USERNAME);
        if (validResponse.currentStatusIsSuccess()) {
            //不存在用户名
            return ServerResponse.createErrorResponseMsg("用户不存在");
        }
        //通过用户名查询quesion
        String quesion = userMapper.selectQuestionByUsername(username);
        //问题内容不为空
        if (StringUtils.isNotBlank(quesion)) {
            //返回问题内容
            return ServerResponse.createSuccessResponseMsg(quesion);
        }
        //未查询到问题内容
        return ServerResponse.createErrorResponseMsg("未能找到问题");
    }

    /**
     * [忘记密码]验证答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> forgetAndVerifyAnswer(String username,String question,String answer){

        int count = userMapper.verifyAnswer(username,question,answer);
        if (count > 0) {
            //用户与问题与答案已对应上
            //使用java的UUID生成token
            String token = UUID.randomUUID().toString();
            //将token放置到本地cache中,使用TokenCache类
            TokenCache.setKey(Constant.TOKEN_PREFIX+username,token);
            return ServerResponse.createSuccessResponse(token);
        } else {
            return ServerResponse.createErrorResponseMsg("答案错误");
        }
    }

    /**
     * [忘记密码]修改密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetAndResetPassword(String username,String newPassword,String forgetToken){
        //检验token是否存在
        if (StringUtils.isBlank(forgetToken)){
            //不存在token
            ServerResponse.createErrorResponseMsg("参数错误,未接收到token");
        }
        //检验用户名是否存在
        ServerResponse validResponse = this.checkValid(username,Constant.USERNAME);
        if (validResponse.currentStatusIsSuccess()) {
            //用户不存在
            return ServerResponse.createErrorResponseMsg("用户不存在");
        }
        //检验token有效性
        //从TokenCache中获得token
        String token = TokenCache.getKey(Constant.TOKEN_PREFIX+username);
        //检验token,isBlank内部判断无字符或字符为空格时返回true
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createErrorResponseMsg("token无效或已过期");
        }
        //验证token是否成功对应
        if (StringUtils.equals(token,forgetToken)) {
            //token匹配正确
            //接收新密码,用MD5加密
            String newMd5password = Md5Utils.Md5EncodeUtf8(newPassword);
            int count = userMapper.updatePasswordByname(username,newMd5password);
            if (count > 0) {
                return ServerResponse.createSuccessResponse("已修改密码");
            }
        } else {
            return ServerResponse.createErrorResponseMsg("token错误,请重新获取");
        }
        return ServerResponse.createErrorResponseMsg("未能修改密码");
    }

    /**
     * 登录状态下更新密码
     * @param oldPassword
     * @param newPassword
     * @param user
     * @return
     */
    @Override
    public ServerResponse loginAndResetPassword(String oldPassword,String newPassword,User user){
        //验证user,防止横向越权,必须指定旧密码是属于该用户
        //原因是在查询count()时不指定id,恶意用户可以通过密码词典匹配任何在数据库存在的密码,使得count>0，从而返回true

        int countAfterCheck = userMapper.checkPassword(user.getId(),Md5Utils.Md5EncodeUtf8(oldPassword));
        //验证当前对象的旧密码正确性
        if (countAfterCheck == 0) {
            return ServerResponse.createErrorResponseMsg("旧密码错误");
        }
        //密码正确则设置新密码
        user.setPassword(Md5Utils.Md5EncodeUtf8(newPassword));
        //更新密码到数据库
        int countAfterUpdate = userMapper.updateByPrimaryKeySelective(user);
        if (countAfterUpdate > 0) {
            return ServerResponse.createSuccessResponse("已更新密码");
        }
        return ServerResponse.createErrorResponseMsg("未能更新密码");
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateUserInfo(User user){
        //查询email,如有查到结果,则代表emil已被占用
        int countEmail = userMapper.checkOtherUsedEmailByUserId(user.getEmail(),user.getId());
        if (countEmail > 0) {
            return ServerResponse.createErrorResponseMsg("该Email被其他用户占用");
        }
        //创建新对象用于选择性更新部分用户信息
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int countUser = userMapper.updateByPrimaryKeySelective(updateUser);
        if (countUser > 0) {
            return ServerResponse.createSuccessResponse("已更新用户信息",updateUser);
        }
        return ServerResponse.createErrorResponseMsg("未能更新用户信息");
    }

    /**
     * 获取用户详细信息
     * @param userId
     * @return
     */
    @Override
    public ServerResponse getUserDetailInfo(Integer userId){
        //根据传入的id查找到对象
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createErrorResponseMsg("未找到当前用户");
        }
        //将密码置空,由于无需用到密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createSuccessResponse(user);
    }




    /*                              管理员操作                              */



    /**
     * 验证管理员身份
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkAdminRole(User user){
        //验证用户已登录并且是管理员账户
        if (user != null && user.getRole().intValue() == Constant.Role.ROLE_ADMIN) {
            return ServerResponse.createSuccessResponse();
        } else {
            return ServerResponse.createErrorResponse();
        }
    }
}
