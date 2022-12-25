package com.botian.food.deliverymanservicemanager.po;

import com.botian.food.deliverymanservicemanager.enummeration.DeliverymanStatus;
import lombok.ToString;

import java.util.Date;

@ToString
public class DeliverymanPO {
    private Integer id;
    private String name;
    private String district;
    private DeliverymanStatus status;
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public DeliverymanStatus getStatus() {
        return status;
    }

    public void setStatus(DeliverymanStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}