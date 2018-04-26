package cn.fmall.service;

import cn.fmall.common.ServerResponse;

public interface ICategoryService {

    //添加分类
    public ServerResponse addCategory(String categoryName, Integer parentId);
}
