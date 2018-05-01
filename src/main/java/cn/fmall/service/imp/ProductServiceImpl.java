package cn.fmall.service.imp;

import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CategoryMapper;
import cn.fmall.dao.ProductMapper;
import cn.fmall.pojo.Category;
import cn.fmall.pojo.Product;
import cn.fmall.service.IProductService;
import cn.fmall.utils.DateTimeUtil;
import cn.fmall.utils.PropertiesUtil;
import cn.fmall.vo.ProductDetailVo;
import cn.fmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

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
        //使用新对象承接传入的id、status,仅用于更新status
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) {
            return ServerResponse.createSuccessResponseMsg("已修改商品在售状态");
        }
        return ServerResponse.createErrorResponseMsg("未能修改商品在售状态");
    }

    /**
     * 获取产品详细信息[后台部分]
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageGetProductDetailInfo(Integer productId){
        if (productId == null) {
            //无ID即参数错误
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createErrorResponseMsg("产品已下架或删除");
        }
        //返回ProductVo对象,先转换product对象为vo对象
        ProductDetailVo vo = assembleAsProductDetailVo(product);
        return ServerResponse.createSuccessResponse(vo);
    }

    /**
     * 获取产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> manegeGetProductList(int pageNum,int pageSize){
        //使用PageHelper完成分页逻辑
        //记录分页的开始,确定分页的起始与大小,本身为拦截器
        PageHelper.startPage(pageNum,pageSize);
        //查询列表的sql,得到pojo对象
        List<Product> productList = productMapper.selectProductList();
        //新建volist对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList) {
            //将product对象转换后并存入productListVo列表对象
            ProductListVo productListVo = assembleProductListVo(productItem);
            //将vo填充进volist
            productListVoList.add(productListVo);
        }
        //分页的收尾
        //包装productList对象为PageInfo
        //传入productList以计算分页
        PageInfo pageResult = new PageInfo(productList);
        //填充所有volist,完成分页数据显示的封装
        pageResult.setList(productListVoList);
        return ServerResponse.createSuccessResponse(pageResult);
    }

    /**
     * 搜索产品
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse manageSearchProduct(int productId,String productName,int pageNum,int pageSize){
        //使用PageHelper完成分页逻辑
        //记录分页的开始,确定分页的起始与大小,本身为拦截器
        PageHelper.startPage(pageNum,pageSize);
        //判断productName是否为空
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        //查询列表的sql,得到pojo对象
        List<Product> productList = productMapper.selectProductByNameAndId(productId,productName);
        //新建volist对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:productList) {
            //将product对象转换后并存入vo列表对象
            ProductListVo productListVo = assembleProductListVo(productItem);
            //将vo填充进volist
            productListVoList.add(productListVo);
        }
        //分页的收尾
        //包装productList对象为PageInfo
        //传入productList以计算分页
        PageInfo pageResult = new PageInfo(productList);
        //填充所有volist,完成分页数据显示的封装
        pageResult.setList(productListVoList);
        return ServerResponse.createSuccessResponse(pageResult);
    }










/*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 非业务逻辑 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    /**
     * ProductDetailVo对象转换器
     * @param product
     * @return
     */
    private ProductDetailVo assembleAsProductDetailVo(Product product){

        ProductDetailVo vo = new ProductDetailVo();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setSubtitle(product.getSubtitle());
        vo.setMainImage(product.getMainImage());
        vo.setSubImages(product.getSubImages());
        vo.setDetail(product.getDetail());
        vo.setPrice(product.getPrice());
        vo.setStock(product.getStock());
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            //如果上级节点为空则代表当前节点为根节点
            vo.setParentCategoryId(0);
        } else {
            vo.setParentCategoryId(category.getParentId());
        }
        //imageHost
        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.nerv.org/"));

        //createTime
        vo.setCreateTime(DateTimeUtil.dateToStringFormat(product.getCreateTime()));

        //updateTime
        vo.setUpdateTime(DateTimeUtil.dateToStringFormat(product.getUpdateTime()));

        return vo;
    }

    /**
     * ProductListVo对象转换器
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo listVo = new ProductListVo();
        listVo.setId(product.getId());
        listVo.setCategoryId(product.getCategoryId());
        listVo.setName(product.getName());
        listVo.setSubtitle(product.getSubtitle());
        listVo.setPrice(product.getPrice());
        listVo.setMainImage(product.getMainImage());
        listVo.setStatus(product.getStatus());
        listVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.nerv.org/"));
        return listVo;
    }

}
