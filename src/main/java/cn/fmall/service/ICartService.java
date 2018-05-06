package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.vo.CartVo;

public interface ICartService {

    //删除购物车商品
    public ServerResponse<CartVo> query(Integer userId);

    //添加商品到购物车
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer productCount);

    //更新购物车商品数量
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer productCount);

    //删除购物车商品
    public ServerResponse<CartVo> delete(Integer userId,String productIds);

    //商品勾选
    public ServerResponse<CartVo> selectOrNot(Integer userId,Integer productId,Integer checked);

    //计算商品总数量
    public ServerResponse<Integer> countProductNum(Integer userId);
}
