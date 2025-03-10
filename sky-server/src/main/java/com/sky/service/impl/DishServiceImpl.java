package com.sky.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional  // 保证事务的一致性
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);


//        向菜品表插入1条数据
        dishMapper.insert(dish);

        //获取inset语句生成的主键值
        Long dishId = dish.getId();

//        向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品的批量删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
//        判断菜品状态，启售则无法删除
        for (Long id : ids) {
            log.info("根据主键查询菜品");
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //启售---无法删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
//        菜品被套餐关联，也无法删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 存在关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);

//        菜品被删除时候，被关联的口味数据也要删除
            dishFlavorMapper.deleteByDishId(id);
        }


    }
}
