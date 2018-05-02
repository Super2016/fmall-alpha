package cn.fmall.controller.backend;

import cn.fmall.common.Constant;
import cn.fmall.common.ResponseCode;
import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Product;
import cn.fmall.pojo.User;
import cn.fmall.service.IFileService;
import cn.fmall.service.IProductService;
import cn.fmall.service.IUserService;
import cn.fmall.utils.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ManageProductController {


    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存商品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save_product.do",method = RequestMethod.POST)
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
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
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

    /**
     * 获取商品详细信息
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_product_detail_info.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getProductDetailInfo(Integer productId,HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //当前用户为空,要求强制登录
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户须登录");
        }
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //当前用户是管理员,返回查询的商品内容
            return iProductService.manageGetProductDetailInfo(productId);
        }
        return ServerResponse.createErrorResponseMsg("当前非管理员权限");
    }

    /**
     * 查找商品列表
     * 使用mybatishelper
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "get_product_list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getProductList(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //当前用户为空,要求强制登录
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户须登录");
        }
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //当前用户是管理员,返回查询商品列表集合
            return iProductService.manegeGetProductList(pageNum, pageSize);
        }
        return ServerResponse.createErrorResponseMsg("当前非管理员权限");
    }

    /**
     * 搜索商品
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "search_product.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse searchProduct(String productName,
                                        @RequestParam(value = "productId",required = false) int productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                        HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //当前用户为空,要求强制登录
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户须登录");
        }
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //当前用户是管理员,返回查询的商品内容
            return iProductService.manageSearchProduct(productId,productName,pageNum,pageSize);
        }
        return ServerResponse.createErrorResponseMsg("当前非管理员权限");
    }

    /**
     * 上传文件
     * @param originFile
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "upload_file.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse uploadFile(@RequestParam(value = "upload_file",required = false) MultipartFile originFile,
                                     HttpServletRequest request,
                                     HttpSession session){
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            //当前用户为空,要求强制登录
            return ServerResponse.createErrorResponse(ResponseCode.NEED_LOGIN.getCode(),"用户须登录");
        }
        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            //当前用户是管理员,返回查询的商品内容
            /* 这里使用SpringMVC的文件上传功能 */
            //通过获取request的sessio的上下文取得realpath
            System.out.println(originFile);
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件,接收返回的目标文件名
            String targetFileName = iFileService.uploadFile(originFile,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            //组装返回值
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            System.out.println("uri>>"+fileMap.get("uri"));
            System.out.println("url>>"+fileMap.get("url"));
            return ServerResponse.createSuccessResponse(fileMap);
        }
        return ServerResponse.createErrorResponseMsg("当前非管理员权限");

    }


    @RequestMapping(value = "upload_richtext_file.do",method = RequestMethod.POST)
    @ResponseBody
    public Map uploadRichtextImgFile(@RequestParam(value = "upload_file",required = false) MultipartFile originFile,
                                     HttpServletRequest request,
                                     HttpSession session,
                                     HttpServletResponse response){
        //返回值按照simditor的格式做
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if (user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }

        if (iUserService.checkAdminRole(user).currentStatusIsSuccess()) {
            /* 这里使用SpringMVC的文件上传功能 */
            //通过获取request的sessio的上下文取得realpath
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件,接收返回的目标文件名
            String targetFileName = iFileService.uploadFile(originFile,path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success",false);
                resultMap.put("msg","富文本文件上传失败");
                return resultMap;
            } else {
                //获得FTP服务器上目标文件的地址
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
                resultMap.put("success",true);
                resultMap.put("msg","富文本文件上传成功");
                resultMap.put("file_path",url);
                //按照simditor的要求,添加响应头
                response.addHeader("Access-Control-Allow-Headers","X-File-Name");
                return resultMap;
            }
        } else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
