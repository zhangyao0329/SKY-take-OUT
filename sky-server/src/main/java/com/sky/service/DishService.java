package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;

public interface DishService {
    /**
     * 新增菜品
     *
     * @param dish
     */
    public void saveWithFlavor(DishDTO dish);
}
