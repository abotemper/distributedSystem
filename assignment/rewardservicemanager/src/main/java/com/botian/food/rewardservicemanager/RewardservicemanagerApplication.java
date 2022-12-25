package com.botian.food.rewardservicemanager;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(value = "com.botian", annotationClass= Mapper.class)
@ComponentScan("com.botian")
public class RewardservicemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RewardservicemanagerApplication.class, args);
	}

}
