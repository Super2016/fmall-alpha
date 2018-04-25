package cn.fmall.dao;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByPrimaryKey(Integer id);

    //查询用户名,统计匹配行数
    int checkUsername(String username);

    //查询email,统计匹配行数
    int checkEmail(String email);

    //通过用户名查找问题
    String selectQuestionByUsername(String username);

    //查询用户名与密码匹配的用户数据,@Param中的值与配置文件中的#{}值对应
    User selectLoginUser(@Param("username") String username,@Param("password") String password);

    //查询验证answer,统计匹配行数
    int verifyAnswer(@Param("username") String username,@Param("question")String question,@Param("answer")String answer);

    //更新用户密码
    int updatePasswordByname(@Param("username")String username,@Param("newPassword")String newPassword);

    //验证密码
    int checkPassword(@Param("userId")int userId,@Param("userId")String password);

    //根据用户id查询email[被其他人使用的email]
    int checkOtherUsedEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}