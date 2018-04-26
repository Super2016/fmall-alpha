package cn.fmall.service;

import cn.fmall.common.ServerResponse;
import cn.fmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    //添加分类
    public ServerResponse addCategory(String categoryName, Integer parentId);

    //更新分类名称
    public ServerResponse setCategoryName(Integer categoryId,String categoryName);

    //获取当前节点下的平级子节点
    public ServerResponse<List<Category>> getChildCategoryParallel(Integer categoryId);

    //递归查找当前节点下所有树形关系子节点的id
    public ServerResponse<List<Integer>> getChildCategoryRecursion(Integer currentCategoryId);
}
