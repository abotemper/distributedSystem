package com.botian.food.restaurantservicemanager.dao;

import com.botian.food.restaurantservicemanager.po.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductDao {

    @Select("SELECT id,name,price, restaurantId,status,date FROM product WHERE id = #{id}")
    ProductPO selsctProduct(Integer id);
}