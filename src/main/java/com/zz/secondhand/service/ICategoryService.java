package com.zz.secondhand.service;

import com.zz.secondhand.common.ServerResponse;

import java.util.List;

public interface ICategoryService {
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
