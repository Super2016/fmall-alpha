package cn.fmall.service.imp;

import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.dao.ProductMapper;
import cn.fmall.pojo.Product;
import cn.fmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加或更新产品
     * @param product
     * @return
     */
    @Override
    public ServerResponse addOrUpdateProduct(Product product){
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                //将子图的地址存入数组
                String[] subImage = product.getSubImages().split(",");
                //当子图有多个时,取第一个子图作为主图
                if (subImage.length > 1) {
                    product.setMainImage(subImage[0]);
                }
            }
            //如果id不为空则判断为更新而非添加商品
            if (product.getId() != null) {
                int count = productMapper.updateByPrimaryKey(product);
                if (count > 0) {
                    return ServerResponse.createSuccessResponseMsg("已更新商品");
                } else {
                    return ServerResponse.createErrorResponseMsg("未能更新商品");
                }
            //id为空代表新增商品
            } else {
                int count = productMapper.insert(product);
                if (count > 0) {
                    return ServerResponse.createSuccessResponseMsg("已添加新商品");
                } else {
                    return ServerResponse.createErrorResponseMsg("未能添加新商品");
                }
            }
        }
        return ServerResponse.createErrorResponseMsg("参数错误[添加或更新产品]");
    }

    /**
     * 修改商品在售状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse setProductSaleStatus(Integer productId,Integer status){
        //如果产品id或在售状态码中任意一值为空,则算作参数异常
        if (productId == null || status == null) {
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        //使用新对象承接部分信息,仅用于更新status
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) {
            return ServerResponse.createSuccessResponseMsg("已修改商品在售状态");
        }
        return ServerResponse.createErrorResponseMsg("未能修改商品在售状态");
    }





}
