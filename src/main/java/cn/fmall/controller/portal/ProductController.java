package cn.fmall.controller.portal;

import cn.fmall.common.ServerResponse;
import cn.fmall.service.IProductService;
import cn.fmall.vo.ProductDetailVo;
import cn.fmall.vo.ProductListVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 获取产品详情
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    /**
     * 搜索产品
     * 通过关键字、产品分类id查找,提供动态排序
     * @param keyWord
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "categoryId",required = false)Integer categoryId,
            @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductByNameAndCategoryId(keyWord,categoryId,pageNum,pageSize,orderBy);
    }
}
