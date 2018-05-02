package cn.fmall.service.imp;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CategoryMapper;
import cn.fmall.dao.ProductMapper;
import cn.fmall.pojo.Category;
import cn.fmall.pojo.Product;
import cn.fmall.service.ICategoryService;
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

import java.util.ArrayList;
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
    @Autowired
    private ICategoryService iCategoryService;

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
     * 获取产品列表[后台部分]
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
     * 搜索产品[后台部分]
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
        //查询列表的sql,得到pojo对象集合
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
        //传入productList以计算分页
        PageInfo pageResult = new PageInfo(productList);
        //填充所有volist,完成分页数据显示的封装填充
        pageResult.setList(productListVoList);
        return ServerResponse.createSuccessResponse(pageResult);
    }




/* <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<前台部分>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/




    /**
     * 获得产品详细信息
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        //当传入的ID为空
        if (productId == null) {
            //无ID即参数错误
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        //当未能查询到产品
        if (product == null) {
            return ServerResponse.createErrorResponseMsg("产品已下架或删除");
        }
        //当查询到产品,但状态非在售
        if (product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createErrorResponseMsg("产品已下架或删除");
        }
        //先转换product对象为vo对象后返回ProductVo对象
        ProductDetailVo vo = assembleAsProductDetailVo(product);
        return ServerResponse.createSuccessResponse(vo);
    }

    /**
     * 查询产品
     * 通过关键字、产品分类id查找,提供动态排序
     * @param keyWord
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductByNameAndCategoryId(String keyWord,
                                                   Integer categoryId,
                                                   int pageNum,
                                                   int pageSize,
                                                   String orderBy){
        if (StringUtils.isBlank(keyWord) && categoryId == null) {
            //校验参数,查询产品关键字与分类ID均是空,代表参数错误
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }

        //新建一个List用户存放所查询到的各分类节点ID
        List<Integer> categoryIdList = new ArrayList<Integer>();

        //>>分类ID不为空时,查询到分类并保存到Category中
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);

            //>>当查询不到分类并且产品关键字也为空即代表未命中数据,返回空结果集
            if (category == null && StringUtils.isBlank(keyWord)) {
                //记录分页开始与单页容量
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> voList = Lists.newArrayList();

                /*  -这里如果传入的是Page对象,将自动返回转换为Page对象并返回
                    -如果传入的是集合对象,则直接返回当前传入的对象
                    -这里如果结果集无变化则直接使用构造器,不填充数据   */
                //传入productList以计算分页
                PageInfo pageInfo = new PageInfo(voList);
                return ServerResponse.createSuccessResponse(pageInfo);
            }
            //调用递归查询分类子节点,获得所需要的各节点的ID
            categoryIdList = iCategoryService.getChildCategoryRecursion(category.getId()).getData();
        }
        //>>当分类ID为空,关键字不为空
        if (StringUtils.isNotBlank(keyWord)) {
            //设置模糊查询参数
            keyWord = new StringBuilder().append("%").append(keyWord).append("%").toString();
        }
        //记录分页开始与单页容量
        PageHelper.startPage(pageNum,pageSize);
        //动态排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            //判断传入的字符串于常量中存在
            if (Constant.ProductListOrderBy.PRICE_ASC_DASC.contains(orderBy)) {
                //将传入的orderBy,如'price_asc'拆分为'price','asc'
                String[] orderByArray = orderBy.split("_");
                //PageHelper传入格式为'price asc',这里使用上面数组拼接
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        //搜索查询Product
        List<Product> productList = productMapper.selectProductByNameAndCategoryIdList(
                categoryIdList.size() == 0 ? null : categoryIdList,
                StringUtils.isBlank(keyWord) ? null : keyWord);
        //创建voList用于接收pojoList转换后的list
        List<ProductListVo> voList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo vo = assembleProductListVo(product);
            voList.add(vo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(voList);
        return ServerResponse.createSuccessResponse(pageInfo);





    }






/*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 非业务逻辑部分 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

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
