package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Product;
import cn.fmall.pojo.User;
import cn.fmall.service.IProductService;
import cn.fmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/profuct/")
public class ManageProductController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    /**
     * 保存商品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save_product.do")
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product){
        //从session域获得当前用户对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //判断用户为空
        if (user == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户须登录后操作");
        }
        //判断管理员用户
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //添加或更新商品逻辑
            return iProductService.addOrUpdateProduct(product);
        } else {
            return ServerResponse.createErrorResponseMsg("当前用户非管理员");
        }
    }

    /**
     * 修改商品在售状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        //从session域获得当前用户对象
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        //判断用户为空
        if (user == null) {
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户须登录后操作");
        }
        //判断管理员用户
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //修改商品在售状态的逻辑
            return iProductService.setProductSaleStatus(productId,status);
        } else {
            return ServerResponse.createErrorResponseMsg("当前用户非管理员");
        }
    }


    public ServerResponse getProductDetailInfo(){
        return null;
    }
}
