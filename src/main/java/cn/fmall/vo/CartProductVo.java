package cn.fmall.vo;

import java.math.BigDecimal;

/**
 * 购物车中产品的vo对象,结合产品与购物车
 */
public class CartProductVo {
    private Integer id; //购物车id
    private Integer userId; //用户id
    private Integer productId; //当前商品id
    private Integer quantity; //当前商品在购物车中的数量
    private Integer productStatus; //商品在售状态
    private Integer productStock; //商品库存
    private Integer productChecked; //当前商品是否被勾选
    private String productName; //商品名
    private String productSubtitle; // 商品副标题
    private String productMainImg; // 商品主图
    private BigDecimal productPrice; // 商品价格
    private BigDecimal totalPrice; // 商品总价

    private String buyQuantityLimit; // 限制可购买数量

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public Integer getProductChecked() {
        return productChecked;
    }

    public void setProductChecked(Integer productChecked) {
        this.productChecked = productChecked;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    public String getProductMainImg() {
        return productMainImg;
    }

    public void setProductMainImg(String productMainImg) {
        this.productMainImg = productMainImg;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBuyQuantityLimit() {
        return buyQuantityLimit;
    }

    public void setBuyQuantityLimit(String buyQuantityLimit) {
        this.buyQuantityLimit = buyQuantityLimit;
    }
}
