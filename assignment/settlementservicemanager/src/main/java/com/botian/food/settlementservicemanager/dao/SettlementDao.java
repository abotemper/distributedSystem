package com.botian.food.settlementservicemanager.dao;

import com.botian.food.settlementservicemanager.po.SettlementPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SettlementDao {

    @Insert("INSERT INTO settlement (orderId, transactionId, status, " +
            "amount,date) VALUES(#{orderId}, #{transactionId}, #{status}, " +
            "#{amount}, #{date})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SettlementPO settlementPO);

}