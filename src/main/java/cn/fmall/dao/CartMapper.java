package cn.fmall.dao;

import cn.fmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserIdandProductIds(@Param("userId")Integer userId,@Param("productIdList")List<String> productIdList);

    int insert(Cart record);

    int insertSelective(Cart record);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    int selectCartProductCheckedByUserId(Integer userId);

    Cart selectByPrimaryKey(Integer id);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int checkedOrNot(@Param("userId")Integer userId,@Param("productId") Integer productId,@Param("checked")Integer checked);

    int countProductNum(Integer userId);
}