package com.botian.food.restaurantservicemanager.dao;

import com.botian.food.restaurantservicemanager.po.RestaurantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RestaurantDao {

    @Select("SELECT id,name,address,status, settlementId,date FROM restaurant WHERE id = #{id}")
    RestaurantPO selectRestaurant(Integer id);
}