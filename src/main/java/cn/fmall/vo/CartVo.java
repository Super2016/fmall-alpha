package cn.fmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车vo
 */
public class CartVo {

    private List<CartProductVo> cartProductVoList;
    private BigDecimal totalPrice;
    private Boolean allChecked;
    private String ImgHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImgHost() {
        return ImgHost;
    }

    public void setImgHost(String imgHost) {
        ImgHost = imgHost;
    }
}