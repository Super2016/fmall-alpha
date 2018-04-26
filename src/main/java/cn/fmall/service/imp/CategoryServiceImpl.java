package cn.fmall.service.imp;

import cn.fmall.common.ServerResponse;
import cn.fmall.dao.CategoryMapper;
import cn.fmall.pojo.Category;
import cn.fmall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * 分类服务
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
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

    /**
     * 更新分类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createErrorResponseMsg("更新分类名参数错误,无产品名或产品id");
        }
        //设置信息到category对象
        Category category = new Category();
        //获得oldCategory
        category = categoryMapper.selectByPrimaryKey(categoryId);
        String oldCategory = category.getName();
        //设置需要修改的值
        category.setId(categoryId);
        category.setName(categoryName);
        //将category对象数据更新到数据库
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createSuccessResponseMsg("已更新分类名称,"+"旧分类名称["+oldCategory+"],新分类名称["+category.getName()+"]");
        } else {
            return ServerResponse.createErrorResponseMsg("未能更新分类名称");
        }
    }

    /**
     * 获取当前节点下的平级子节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildCategoryParallel(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectChildCategoryByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的自分类");
        }
        return ServerResponse.createSuccessResponse(categoryList);
    }

    /**
     * 递归查询本节点与树形关系子节点的id
     * @param currentCategoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> getChildCategoryRecursion(Integer currentCategoryId){
        //初始化Set集合,使用Guava库创建集合实例
        Set<Category> categorySet = Sets.newHashSet();

        //最后返回一棵节点树
        recursionFindChild(currentCategoryId,categorySet);

        //初始化List集合,使用Guava库创建id的List集合实例
        List<Integer> categoryIdList = Lists.newArrayList();

        if (currentCategoryId != null) {
            //当前节点不为空,遍历Set集合的节点
            for (Category categoryItem:categorySet) {
                //每查到一个节点,将它的id存入list集合
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createSuccessResponse(categoryIdList);
    }

    /**
     * 递归算法
     * 使用Set集合实现排重
     * 实现排重需要对象重写hashCode()与equal()方法,Integer,String等基本对象重写了
     * Category非基本对象,须重写hashCode()与equal()方法
     * @param currentCategoryId
     * @param categorySet
     * @return
     */
    private Set<Category> recursionFindChild(Integer currentCategoryId,Set<Category> categorySet){
        //查询当前所在节点
        Category category = categoryMapper.selectByPrimaryKey(currentCategoryId);
        if (category != null) {
            //添加当前节点到Set集合
            categorySet.add(category);
        }
        //查询当前节点的平级子节点
        List<Category> categoryList = categoryMapper.selectChildCategoryByParentId(currentCategoryId);
        //mybatis不会返回null对象,这里无需判断categoryList对象为null
        //递归算法必须有退出条件,条件是查询到当前节点为空
        for (Category categoryItem : categoryList) {
            //开始递归
            //一直向更深层级节点查找
            //每查找到一个节点,将其存入Set集合,这里将存入以当前节点为顶点的节点树
            recursionFindChild(categoryItem.getId(),categorySet);
        }
        return categorySet;
    }


}
