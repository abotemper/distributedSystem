package com.botian.food.orderservicemanager.dao;

import com.botian.food.orderservicemanager.po.OrderDetailPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderDetailDao {

    @Insert("INSERT INTO order_detail (status, address, accountId, productId, deliverymanId, settlementId, " +
            "rewardId, price, date) VALUES(#{status}, #{address},#{accountId},#{productId},#{deliverymanId}," +
            "#{settlementId}, #{rewardId},#{price}, #{date})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderDetailPO orderDetailPO);

    @Update("update order_detail set status =#{status}, address =#{address}, accountId =#{accountId}, " +
            "productId =#{productId}, deliverymanId =#{deliverymanId}, settlementId =#{settlementId}, " +
            "rewardId =#{rewardId}, price =#{price}, date =#{date} where id=#{id}")
    void update(OrderDetailPO orderDetailPO);

    @Select("SELECT id,status,address, accountId, productId, deliverymanId," +
            "settlementId, rewardId, price, date FROM order_detail WHERE id = #{id}")
    OrderDetailPO selectOrder(Integer id);
}
