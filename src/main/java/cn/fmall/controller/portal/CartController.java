package cn.fmall.controller.portal;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.User;
import cn.fmall.service.ICartService;
import cn.fmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 购物车服务
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService  iCartService;


    /**
     * 添加商品
     * @param count
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> addCart(Integer count,Integer productId,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    /**
     * 更新商品数量
     * @param count
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "update.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> updateCart(Integer count,Integer productId,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    /**
     * 删除商品
     * @param productIds
     * @param session
     * @return
     */
    @RequestMapping(value = "delete.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> deleteCart(String productIds,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.delete(user.getId(),productIds);
    }

    @RequestMapping(value = "query.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> queryCart(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.query(user.getId());
    }

    /**
     * 全选
     * @param session
     * @return
     */
    @RequestMapping(value = "select_all.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.selectOrNot(
                user.getId(),
                null,
                Constant.CartCheckedStatus.CHECKED);
    }

    /**
     * 全不选
     * @param session
     * @return
     */
    @RequestMapping(value = "unselect_all.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> unselectAll(HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.selectOrNot(
                user.getId(),
                null,
                Constant.CartCheckedStatus.UN_CHECKED);
    }

    /**
     * 选择性勾选
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "select.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> select(Integer productId,Integer checked,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.selectOrNot(
                user.getId(),
                productId,
                Constant.CartCheckedStatus.CHECKED);
    }

    /**
     * 选择性不勾选
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "unselect.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> unselect(Integer productId,Integer checked,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //需要强制登录
            return ServerResponse.createErrorResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDescription());
        }
        return iCartService.selectOrNot(
                user.getId(),
                productId,
                Constant.CartCheckedStatus.UN_CHECKED);
    }

    @RequestMapping(value = "count.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse countProductNum(Integer productId,Integer checked,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createSuccessResponseMsg("0");
        }
        return iCartService.countProductNum(user.getId());
    }
}
