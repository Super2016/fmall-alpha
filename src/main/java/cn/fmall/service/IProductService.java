package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Product;

/**
 * 产品接口
 */
public interface IProductService {

    //添加或修改商品
    public ServerResponse addOrUpdateProduct(Product product);

    //修改商品在售状态
    public ServerResponse setProductSaleStatus(Integer productId,Integer status);
}
