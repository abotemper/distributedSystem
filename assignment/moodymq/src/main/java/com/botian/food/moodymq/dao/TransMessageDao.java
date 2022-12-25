package com.botian.food.moodymq.dao;


import com.botian.food.moodymq.po.TransMessagePO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TransMessageDao {

    @Insert("replace INTO trans_message (id ,type, service, exchange, routingKey, queue, sequence, payload, date) " +
            "VALUES(#{id}, #{type}, #{service},#{exchange},#{routingKey},#{queue},#{sequence}, #{payload},#{date})")
    void insert(TransMessagePO transMessagePO);

    @Update("update trans_message set type =#{type}, service =#{service}, exchange =#{exchange}, " +
            "routingKey =#{routingKey}, queue =#{queue}, sequence =#{sequence}, payload =#{payload}, date =#{date} " +
            "where id=#{id} and service = #{service}")
    void update(TransMessagePO transMessagePO);

    @Select("SELECT id, type, service, exchange,routingKey, queue, sequence, payload, date FROM " +
            "trans_message " +
            "WHERE id = #{id} and service = #{service}")
    TransMessagePO selectByIdAndService(@Param("id") String id, @Param("service") String service);

    @Select("SELECT id, type, service, exchange, routingKey, queue, sequence, payload, date FROM " +
            "trans_message WHERE type = #{type} and service = #{service}")
    List<TransMessagePO> selectByTypeAndService(@Param("type") String type, @Param("service") String service);

    @Delete("DELETE FROM trans_message WHERE id = #{id} and service = #{service}")
    void delete(@Param("id") String id, @Param("service") String service);

}