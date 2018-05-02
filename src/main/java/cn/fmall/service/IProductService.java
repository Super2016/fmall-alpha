package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Product;
import cn.fmall.vo.ProductDetailVo;
import cn.fmall.vo.ProductListVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 产品接口
 */
public interface IProductService {

    //添加或修改商品
    public ServerResponse addOrUpdateProduct(Product product);

    //修改商品在售状态
    public ServerResponse setProductSaleStatus(Integer productId,Integer status);


    //获取产品详细信息[后台部分]
    public ServerResponse<ProductDetailVo> manageGetProductDetailInfo(Integer productId);

    //获取产品列表[后台部分]
    public ServerResponse<PageInfo> manegeGetProductList(int pageNum, int pageSize);

    //搜索产品[后台部分]
    public ServerResponse manageSearchProduct(int productId,String productName,int pageNum,int pageSize);

    //获取产品详细信息[前台部分]
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    //获取产品列表[前台部分]
    public ServerResponse<PageInfo> getProductByNameAndCategoryId(String productName,Integer categpryId,int pageNum,int pageSize,String orderBy);
}
