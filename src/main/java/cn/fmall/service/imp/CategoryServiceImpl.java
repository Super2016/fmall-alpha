package cn.fmall.service.imp;

import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CategoryMapper;
import cn.fmall.pojo.Category;
import cn.fmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * 分类服务
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     * @param categoryName
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createErrorResponseMsg("添加产品参数错误,无产品名或上一节点");
        }
        //信息存入分类对象
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true); // 表示该分类可用

        //添加分类到数据库
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createSuccessResponse("添加品类成功","品类["+category.getName()+"]");
        } else {
            return ServerResponse.createErrorResponseMsg("添加品类失败");
        }
    }









}
