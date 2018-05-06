package cn.fmall.service.imp;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CartMapper;
import cn.fmall.dao.ProductMapper;
import cn.fmall.pojo.Cart;
import cn.fmall.pojo.Product;
import cn.fmall.service.ICartService;
import cn.fmall.utils.BigDecimalUtil;
import cn.fmall.utils.PropertiesUtil;
import cn.fmall.vo.CartProductVo;
import cn.fmall.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.Caret;
import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询购物车
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVo> query(Integer userId) {
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createSuccessResponse(cartVo);
    }

    /**
     * 添加购物车商品条目数
     * @param userId        用户ID
     * @param productId     商品ID
     * @param productCount  商品数量
     * @return
     */
    @Override
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer productCount){
        if (productId == null || productCount == null) {
            return ServerResponse.createErrorResponse(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }

        //查询当前购物车是否存在当前商品
        Cart cartProductItem = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if (cartProductItem == null) {
            //购物车中无当前商品,新增一个条目
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(productCount);
            cartItem.setChecked(Constant.CartCheckedStatus.CHECKED);
            //添加该购物车的新商品记录到数据库
            cartMapper.insert(cartItem);
        } else {
            //购物车中存在当前商品,增加该商品的数量值
            productCount = cartProductItem.getQuantity()+productCount;
            cartProductItem.setQuantity(productCount);
            //更新购物车到数据库
            cartMapper.updateByPrimaryKeySelective(cartProductItem);
        }
        //将购物车结果返回到前端页面展示
        return this.query(userId);
    }

    /**
     * 更新购物车商品数量
     * @param userId
     * @param productId
     * @param productCount
     * @return
     */
    @Override
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer productCount){
        if (productId == null || productCount == null) {
            return ServerResponse.createErrorResponse(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);//查询购物车中是否有此商品
        if (cart != null) {
            cart.setQuantity(productCount);//设置该购物车商品数量
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.query(userId);
    }

    /**
     * 删除购物车商品
     * @param userId
     * @param productIds
     * @return
     */
    @Override
    public ServerResponse<CartVo> delete(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (productList.isEmpty()) {
            return ServerResponse.createErrorResponse(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        cartMapper.deleteByUserIdandProductIds(userId,productList);
        return this.query(userId);
    }

    /**
     * 选择或反选商品
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectOrNot(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrNot(userId,productId,checked);//更新勾选状态
        return this.query(userId);
    }

    /**
     * 计算商品总数量
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<Integer> countProductNum(Integer userId){
        if (userId == null) {
            return ServerResponse.createSuccessResponse(0);
        }
        int count = cartMapper.countProductNum(userId);
        return ServerResponse.createSuccessResponse(count);
    }


    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<业务逻辑分离部分>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    /**
     * 获取CartVo对象,商品可选数量被限制为最大库存量
     * @param userId
     * @return
     */
    private CartVo getCartVo(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);//查询购物车中商品列数
        List<CartProductVo> cartProductVoList = Lists.newArrayList();//创建商品集合用于放入购物车中
        BigDecimal totalPrice = new BigDecimal("0");//初始化总价格
        if (CollectionUtils.isNotEmpty(cartList)) {//如果购物车中列数不为空
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();//用于组装CartProductVo
                //遍历购物车条目,将每个pojo内容填充到vo
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                //查询到商品信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    //将product内容填充到vo
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImg(product.getSubImages());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());

                    int buyQuantity = 0;    //初始化可购买数量
                    //判断库存是否足够
                    if (product.getStock() >= cartItem.getQuantity()) {
                        buyQuantity = cartItem.getQuantity();   //库存足够
                        cartProductVo.setBuyQuantityLimit(Constant.StockEnough.STOCK_ENOUGH);
                    } else {
                        //库存不够
                        cartProductVo.setBuyQuantityLimit(Constant.StockEnough.STOCK_LACK);
                        buyQuantity = product.getStock();   //设置最大有效数量
                        Cart cartValidQuantity =  new Cart();   //在购物车中更新有效购买数量
                        cartValidQuantity.setId(cartItem.getId());
                        cartValidQuantity.setQuantity(buyQuantity);
                        cartMapper.updateByPrimaryKeySelective(cartValidQuantity);
                    }
                    cartProductVo.setQuantity(buyQuantity);//设置前端页面显示的商品已选数量
                    cartProductVo.setTotalPrice(    //计算该商品总价
                            BigDecimalUtil.multip(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());//设置为已勾选状态
                }
                if (cartItem.getChecked() == Constant.CartCheckedStatus.CHECKED) {
                    totalPrice = BigDecimalUtil.add(//如果已勾选,叠加该所有商品总价到购物车总价中
                            totalPrice.doubleValue(),
                            cartProductVo.getTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);//遍历购物车商品集合pojo,将商品VO加入商品集合VO中
            }
            //填充购物车VO,包括总价,全选状态,服务器前缀,以及商品集合
            cartVo.setTotalPrice(totalPrice);
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setAllChecked(this.allCheckedStatus(userId));
            cartVo.setImgHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
            return cartVo;
        } else {
            return null;
        }
    }

    /**
     * 检查是否所有商品被全选
     * @param userId
     * @return
     */
    private boolean allCheckedStatus(Integer userId){
        if (userId == null) {
            return false;
        } else {
            boolean status = cartMapper.selectCartProductCheckedByUserId(userId) == 0;
            return status;
        }
    }
}
